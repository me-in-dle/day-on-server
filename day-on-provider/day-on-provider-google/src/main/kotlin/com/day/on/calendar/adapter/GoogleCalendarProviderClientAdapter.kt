package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.client.GoogleCalendarFeign
import com.day.on.calendar.client.GoogleOauthFeign
import com.day.on.calendar.dto.GoogleCalendarEvent
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.time.*
import java.time.format.DateTimeFormatter

/*
* 구글 토큰 호출
* 구글 auth code -> token 을 저장하는 어댑터
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

    override fun fetchEventsForMonth(connectType: ConnectType, accessToken: String, year: Int, month: Int): List<ScheduleContent> {
        // 1) 먼저 calendarList 조회해서 primary 캘린더 id와 timezone을 결정
        val authHeader = "Bearer $accessToken"
        val calListResp = calendarClient.listCalendarList(authHeader)
        val calendarEntry = calListResp.items.firstOrNull { it.primary == true }
                ?: calListResp.items.firstOrNull()
                ?: throw IllegalStateException("No calendar available for user")

        val calendarId = calendarEntry.id
        val calendarTimeZone = calendarEntry.timeZone ?: "UTC"

        // 2) time range: 사용자의 캘린더 timeZone 기준으로 월 시작/종료를 계산한 뒤 UTC로 변환
        val ym = YearMonth.of(year, month)
        val zone = try { ZoneId.of(calendarTimeZone) } catch (_: Exception) { ZoneOffset.UTC }
        val startZdt = ym.atDay(1).atStartOfDay(zone).withZoneSameInstant(ZoneOffset.UTC)
        val endZdt = ym.atEndOfMonth().atTime(23, 59, 59).atZone(zone).withZoneSameInstant(ZoneOffset.UTC)

        logger.info("Fetching Google events calendarId={}, tz={}, timeMin={}, timeMax={}", calendarId, calendarTimeZone, startZdt, endZdt)

        val results = mutableListOf<ScheduleContent>()
        var pageToken: String? = null

        do {
            val resp = calendarClient.listEvents(
                    calendarId = java.net.URLEncoder.encode(calendarId, Charsets.UTF_8.name()),
                    authorization = authHeader,
                    timeMin = startZdt.format(rfc3339),
                    timeMax = endZdt.format(rfc3339),
                    pageToken = pageToken
            )

            logger.debug("Google listEvents result: items.size={}, nextPageToken={}", resp.items.size, resp.nextPageToken)

            resp.items.forEach { ge ->
                results.add(mapGoogleEventToScheduleContent(ge))
            }
            pageToken = resp.nextPageToken
        } while (!pageToken.isNullOrBlank())

        return results
    }

    private fun mapGoogleEventToScheduleContent(ge: GoogleCalendarEvent): ScheduleContent {
        // DB에서 ID를 발급하므로 여기선 0L 사용
        val (startTime, endTime) = parseStartEndToLocalTimes(ge)

        return ScheduleContent(
                id = 0L,
                dailySchedulesId = 0L,
                accountId = 0L, // saveMonthly에서 accountId로 대체될 예정
                relationTypes = ConnectType.GOOGLE,
                title = ge.summary ?: "",
                location = ge.location,
                contents = ge.description,
                useYn = "Y",
                tagIds = null,
                endTime = endTime,
                startTime = startTime,
                status = TaskStatus.IN_PROGRESS,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )
    }

    private fun parseStartEndToLocalTimes(ge: GoogleCalendarEvent): Pair<LocalTime, LocalTime> {
        // 우선 시도: dateTime (with offset) -> parse as OffsetDateTime -> convert to LocalTime (UTC 혹은 서버 기준)
        val startRaw = ge.start?.get("dateTime") ?: ge.start?.get("date")
        val endRaw = ge.end?.get("dateTime") ?: ge.end?.get("date")

        fun toLocalTime(raw: String?): LocalTime {
            if (raw == null) return LocalTime.MIDNIGHT
            return try {
                if (raw.contains("T")) {
                    // DateTime with offset or zone
                    val odt = OffsetDateTime.parse(raw)
                    odt.toLocalTime()
                } else {
                    // All-day event (date only) -> start: 00:00, end: 23:59
                    LocalTime.MIDNIGHT
                }
            } catch (ex: Exception) {
                // Fallback: parse as LocalDate
                try {
                    LocalDate.parse(raw).atStartOfDay().toLocalTime()
                } catch (_: Exception) {
                    LocalTime.MIDNIGHT
                }
            }
        }

        val s = toLocalTime(startRaw)
        val e = if (endRaw != null && !endRaw.contains("T")) {
            // end as date -> set end to 23:59 to represent whole day
            LocalTime.of(23, 59)
        } else {
            toLocalTime(endRaw)
        }

        return Pair(s, e)
    }
}