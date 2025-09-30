package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.client.GoogleCalendarFeign
import com.day.on.calendar.client.GoogleOauthFeign
import com.day.on.calendar.dto.GoogleCalendarEvent
import com.day.on.calendar.dto.GoogleCalendarListEntry
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import com.day.on.calendar.type.CalendarIdType
import com.day.on.calendar.usecase.dto.GoogleWatchRequest
import com.day.on.calendar.usecase.dto.GoogleWatchResponse
import com.day.on.calendar.usecase.dto.ProviderEventChange
import com.day.on.calendar.usecase.dto.ProviderEventsResponse
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import com.day.on.calendar.usecase.outbound.CalendarTokenPort
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.net.URLEncoder
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/*
* 구글 토큰 호출
* 구글 auth code -> token 을 저장하는 어댑터
* 이벤트 리스트를 호출하는 어댑터
*/
@Component
class GoogleCalendarProviderClientAdapter(
    private val oauthClient: GoogleOauthFeign,
    private val calendarClient: GoogleCalendarFeign,
    private val tokenPort: CalendarTokenPort,
    private val googleProps: GoogleCalendarOauthProperties,
) : CalendarProviderClientPort {
    // TODO : 시간정책 UTC 저장 or 클라이언트 변환 규칙을 정하기
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun exchangeCodeForToken(
        connectType: ConnectType,
        code: String,
        redirectUri: String,
    ): CalendarTokens {
        val form =
            LinkedMultiValueMap<String, String>().apply {
                add("client_id", googleProps.clientId)
                add("client_secret", googleProps.clientSecret)
                add("code", code)
                add("redirect_uri", redirectUri)
                add("grant_type", "authorization_code")
            }
        val resp = oauthClient.exchangeTokenForm(form)

        if (resp.accessToken == null) {
            throw IllegalStateException("Google token(for calendar) exchange failed: no access token")
        }

        val expiresAt = LocalDateTime.now().plusSeconds(resp.expiresIn!!.toLong())

        return CalendarTokens(
            accountId = 0L, // 실제 저장 시 service에서 accountId 채움: tokenPort.save(token.copy(accountId = accountId))
            connectType = ConnectType.GOOGLE,
            accessToken = resp.accessToken,
            refreshToken =
                resp.refreshToken
                    ?: "",
            expiresAt = expiresAt,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    override fun getRefreshToken(accountId: Long, connectType: ConnectType, refreshToken: String): CalendarTokens  {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("client_id", googleProps.clientId)
            add("client_secret", googleProps.clientSecret)
            add("refresh_token", refreshToken)
            add("grant_type", "refresh_token")
        }

        val resp = oauthClient.exchangeTokenForm(form)

        requireNotNull(resp.accessToken) { "Google refreshToken response did not contain access_token" }
        requireNotNull(resp.expiresIn) { "Google refreshToken response did not contain expires_in" }

        val refreshed = CalendarTokens(
            accountId = accountId,
            connectType = connectType,
            accessToken = resp.accessToken,
            refreshToken = refreshToken,
            expiresAt = LocalDateTime.now().plusSeconds(resp.expiresIn.toLong()),
            updatedAt = LocalDateTime.now()
        )

        tokenPort.save(refreshed)
        return refreshed
    }

    override fun fetchEventsForDateRange(
        connectType: ConnectType,
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Pair<List<Pair<LocalDate, ScheduleContent>>, String?> {
        val authHeader = "Bearer $accessToken"
        val calListResp = calendarClient.listCalendarList(authHeader)

        val primaryCalendar = calListResp.items.firstOrNull { it.primary == true }

        if (primaryCalendar == null) {
            logger.warn("[fetchEventsForDateRange] : primaray 캘린더가 없습니다.")
            return emptyList<Pair<LocalDate, ScheduleContent>>() to null
        }
        logger.info("엑세스토큰 !!!!!!! ${accessToken}")
        logger.info("Syncing primary calendar: ${primaryCalendar.summary}")

        return try {
            fetchEventsFromCalendar(primaryCalendar, authHeader, startDate, endDate)
        } catch (e: Exception) {
            logger.warn("Failed to fetch from primary calendar: ${primaryCalendar.summary}", e)
            emptyList<Pair<LocalDate, ScheduleContent>>() to null
        }
    }

    private fun fetchEventsFromCalendar(
        calendar: GoogleCalendarListEntry,
        authHeader: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Pair<List<Pair<LocalDate, ScheduleContent>>, String?> {
        val calendarId = calendar.id
        val results = mutableListOf<Pair<LocalDate, ScheduleContent>>()
        var pageToken: String? = null
        var nextSyncToken: String? = null

        while (true) {
            val resp =
                calendarClient.listEvents(
                    calendarId = URLEncoder.encode(calendarId, "UTF-8"),
                    authorization = authHeader,
                    singleEvents = true,
                    pageToken = pageToken,
                    maxResults = 2500,
                    showDeleted = true
                )
            logger.info("Response nextSyncToken: ${resp.nextSyncToken}")
            logger.info("Response nextPageToken: ${resp.nextPageToken}")

            resp.items.forEach { ge ->
                if (ge.status == "cancelled") return@forEach
                val eventDate = extractEventDate(ge)
                // 기간 필터링 후 저장
                if (!eventDate.isBefore(startDate) && !eventDate.isAfter(endDate)) {
                    val content = toScheduleContent(ge, calendar.summary)
                    results.add(eventDate to content)
                }
            }

            pageToken = resp.nextPageToken
            nextSyncToken = resp.nextSyncToken
            if (pageToken.isNullOrBlank()) break
        }
        // 다음 페이지가 있으면 반복
        logger.info("Final nextSyncToken: $nextSyncToken")
        if (nextSyncToken == null) {
            logger.warn("No syncToken returned from Google (calendarId=${calendar.id})")
        }
        return results to nextSyncToken
    }

    private fun mapGoogleEventToScheduleContent(
        ge: GoogleCalendarEvent,
        calendarName: String?,
    ): Pair<LocalDate, ScheduleContent> {
        logger.debug(
            "Google Event fetched: id=${ge.id}, summary=${ge.summary}, " + "start=${ge.start}, end=${ge.end}, location=${ge.location}, desc=${ge.description}",
        )
        val eventDate = extractEventDate(ge)
        val content = toScheduleContent(ge, calendarName)

        return eventDate to content
    }

    private fun toScheduleContent(
        ge: GoogleCalendarEvent,
        calendarName: String?,
    ): ScheduleContent {
        val (startTime, endTime) = parseStartEndToLocalTimes(ge)
        val now = LocalDateTime.now()

        return ScheduleContent(
            id = 0L,
            dailySchedulesId = 0L,
            accountId = 0L,
            externalEventId = ge.id ?: UUID.randomUUID().toString(),
            relationTypes = ConnectType.GOOGLE,
            title = ge.summary.toString(),
            location = ge.location,
            contents = ge.description,
            useYn = "Y",
            tagIds = "Google",
            startTime = startTime,
            endTime = endTime,
            status = determineStatus(calendarName),
            createdAt = now,
            updatedAt = now,
        )
    }


    /** 이벤트 날짜 추출 */
    private fun extractEventDate(ge: GoogleCalendarEvent): LocalDate {
        return ge.start?.get("dateTime")?.let {
            OffsetDateTime.parse(it).toLocalDate()
        } ?: ge.start?.get("date")?.let {
            LocalDate.parse(it)
        } ?: LocalDate.now()
    }

    /** 상태 결정 */
    private fun determineStatus(calendarName: String?): TaskStatus {
        return if (calendarName?.contains("task", ignoreCase = true) == true) {
            TaskStatus.TODO
        } else {
            TaskStatus.PENDING
        }
    }

    /**
     * Google의 start/end를 LocalTime으로 변환
     */
    private fun parseStartEndToLocalTimes(ge: GoogleCalendarEvent): Pair<LocalTime, LocalTime> {
        val startRaw = ge.start?.get("dateTime") ?: ge.start?.get("date")
        val endRaw = ge.end?.get("dateTime") ?: ge.end?.get("date")

        fun toLocalTime(raw: String?): LocalTime {
            if (raw == null) return LocalTime.MIDNIGHT

            return try {
                if (raw.contains("T")) {
                    // DateTime 형식 (2025-01-15T10:00:00+09:00)
                    OffsetDateTime.parse(raw).toLocalTime()
                } else {
                    // Date 형식 (2025-01-15) - 종일 일정
                    LocalTime.MIDNIGHT
                }
            } catch (ex: Exception) {
                logger.warn("Failed to parse time: $raw", ex)
                LocalTime.MIDNIGHT
            }
        }

        val startTime = toLocalTime(startRaw)
        val endTime =
            if (endRaw != null && !endRaw.contains("T")) {
                // 종일 일정의 경우 끝 시간을 23:59로 설정
                LocalTime.of(23, 59)
            } else {
                toLocalTime(endRaw)
            }

        return Pair(startTime, endTime)
    }

    override fun fetchEventsWithSyncToken(
        connectType: ConnectType,
        accessToken: String,
        syncToken: String?,
    ): ProviderEventsResponse {
        if (syncToken.isNullOrBlank()) {
            logger.warn("syncToken is null or empty - cannot perform incremental sync")
            return ProviderEventsResponse(emptyList(), null)
        }

        val authHeader = "Bearer $accessToken"
        val calListResp = calendarClient.listCalendarList(authHeader)
        val primaryCalendar =
            calListResp.items.firstOrNull { it.primary == true }
                ?: return ProviderEventsResponse(emptyList(), syncToken)

        var pageToken: String? = null
        val changes = mutableListOf<ProviderEventChange>()
        var newSyncToken: String? = null

        // TODO : 410 처리 로직
        try {
            while (true) {
                val resp =
                    calendarClient.listEventsWithSyncToken(
                        calendarId = URLEncoder.encode(primaryCalendar.id, "UTF-8"),
                        authorization = authHeader,
                        syncToken = syncToken,
                        pageToken = pageToken,
                        maxResults = 2500,
                        singleEvents = true,
                        showDeleted = true
                    )

                resp.items.forEach { ge ->
                    val (eventDate, content) = mapGoogleEventToScheduleContent(ge, primaryCalendar.summary)
                    changes += ProviderEventChange(
                        externalEventId = ge.id ?: UUID.randomUUID().toString(),
                        status = ge.status ?: "confirmed",
                        eventDate = eventDate,
                        schedule = if (ge.status == "cancelled") null else content,
                    )
                }

                newSyncToken = resp.nextSyncToken
                pageToken = resp.nextPageToken
                if (pageToken.isNullOrBlank()) break
            }
        } catch (ex: feign.FeignException) {
            if (ex.status() == 410) {
                logger.warn("SyncToKen 만료 됨  syncToken=$syncToken")
                // TODO : 서비스레벨에서 다시 full sync 트리거하도록 처리
            }
            throw ex
        }

        return ProviderEventsResponse(changes, newSyncToken ?: syncToken)
    }

    override fun registerWatch(
        connectType: ConnectType,
        accessToken: String,
        calendarId: CalendarIdType,
        callbackUrl: String,
    ): GoogleWatchResponse {
        val request = GoogleWatchRequest(id = UUID.randomUUID().toString(), address = callbackUrl)

        return calendarClient.watchCalendar(authorization = "Bearer $accessToken", calendarId = calendarId.value, body = request)
    }
}
