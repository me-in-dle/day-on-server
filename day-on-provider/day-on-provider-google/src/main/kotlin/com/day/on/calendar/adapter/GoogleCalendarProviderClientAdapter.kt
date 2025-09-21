package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.client.GoogleCalendarFeign
import com.day.on.calendar.client.GoogleOauthFeign
import com.day.on.calendar.dto.GoogleCalendarEvent
import com.day.on.calendar.dto.GoogleCalendarListEntry
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.net.URLEncoder
import java.time.*
import java.time.format.DateTimeFormatter

/*
* 구글 토큰 호출
* 구글 auth code -> token 을 저장하는 어댑터
* 이벤트 리스트를 호출하는 어댑터
*/
@Component
class GoogleCalendarProviderClientAdapter (
        private val oauthClient: GoogleOauthFeign,
        private val calendarClient: GoogleCalendarFeign,
        private val googleProps: GoogleCalendarOauthProperties
) : CalendarProviderClientPort {
    // TODO : 시간정책 UTC 저장 or 클라이언트 변환 규칙을 정하기
    private val rfc3339 = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun exchangeCodeForToken(connectType: ConnectType, code: String, redirectUri: String): CalendarTokens {
        val form = LinkedMultiValueMap<String, String>().apply {
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

        return CalendarTokens(
                accountId = 0L, // 실제 저장 시 service에서 accountId 채움: tokenPort.save(token.copy(accountId = accountId))
                connectType = ConnectType.GOOGLE,
                accessToken = resp.accessToken,
                refreshToken = resp.refreshToken ?: "",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )
    }

    override fun fetchEventsForDateRange(
            connectType: ConnectType,
            accessToken: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): List<Pair<LocalDate, ScheduleContent>> {
        val authHeader = "Bearer $accessToken"
        val calListResp = calendarClient.listCalendarList(authHeader)

        val primaryCalendar = calListResp.items.firstOrNull { it.primary == true }

        if (primaryCalendar == null) {
            logger.warn("[fetchEventsForDateRange] : primaray 캘린더가 없습니다.")
            return emptyList()
        }

        logger.info("Syncing primary calendar: ${primaryCalendar.summary}")

        return try {
            fetchEventsFromCalendar(primaryCalendar, authHeader, startDate, endDate)
        } catch (e: Exception) {
            logger.warn("Failed to fetch from primary calendar: ${primaryCalendar.summary}", e)
            emptyList()
        }
    }

    private fun fetchEventsFromCalendar(
            calendar: GoogleCalendarListEntry,
            authHeader: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): List<Pair<LocalDate, ScheduleContent>> {
        val calendarId = calendar.id
        val zone = try {
            ZoneId.of(calendar.timeZone ?: "UTC")  // 구글 캘린더의 타임존
        } catch (_: Exception) {
            ZoneOffset.UTC
        }

        // 동기화할 시간 범위 (UTC 기준으로 변환)
        val startZdt = startDate.atStartOfDay(zone).withZoneSameInstant(ZoneOffset.UTC)
        val endZdt = endDate.atTime(23, 59, 59).atZone(zone).withZoneSameInstant(ZoneOffset.UTC)

        val results = mutableListOf<Pair<LocalDate, ScheduleContent>>()
        var pageToken: String? = null

        while (true) {
            val resp = calendarClient.listEvents(
                    calendarId = URLEncoder.encode(calendarId, "UTF-8"),
                    authorization = authHeader,
                    timeMin = startZdt.format(rfc3339),
                    timeMax = endZdt.format(rfc3339),
                    pageToken = pageToken,
                    maxResults = 250
            )

            resp.items.forEach { ge ->
                results.add(mapGoogleEventToScheduleContent(ge, calendar.summary))
            }

            pageToken = resp.nextPageToken
            if (pageToken.isNullOrBlank()) break
        }
        // 다음 페이지가 있으면 반복

        return results
    }


    private fun mapGoogleEventToScheduleContent(
            ge: GoogleCalendarEvent,
            calendarName: String?
    ): Pair<LocalDate, ScheduleContent> {

        logger.debug("Google Event fetched: id=${ge.id}, summary=${ge.summary}, " +
                "start=${ge.start}, end=${ge.end}, location=${ge.location}, desc=${ge.description}")

        val (startTime, endTime) = parseStartEndToLocalTimes(ge)
        val eventDate = extractEventDate(ge)

        val content = ScheduleContent(
                id = 0L,
                dailySchedulesId = 0L, // Adapter 단계에서 채움
                accountId = 0L,        // Adapter 단계에서 채움
                relationTypes = ConnectType.GOOGLE,
                title = ge.summary ?: "(제목 없음)",
                location = ge.location,
                contents = ge.description,
                useYn = "Y",
                tagIds = "Google",
                startTime = startTime,
                endTime = endTime,
                status = determineStatus(calendarName),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )

        return eventDate to content
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
            TaskStatus.IN_PROGRESS
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
        val endTime = if (endRaw != null && !endRaw.contains("T")) {
            // 종일 일정의 경우 끝 시간을 23:59로 설정
            LocalTime.of(23, 59)
        } else {
            toLocalTime(endRaw)
        }

        return Pair(startTime, endTime)
    }
}