package com.day.on.calendar.usecase.outbound

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.type.CalendarIdType
import com.day.on.calendar.usecase.dto.GoogleWatchResponse
import com.day.on.calendar.usecase.dto.ProviderEventsResponse
import java.time.LocalDate

interface CalendarProviderClientPort {
    // 외부 캘린더 호출 코드→토큰 교환 및 이벤트 조회 포트
    fun exchangeCodeForToken(
        connectType: ConnectType,
        code: String,
        redirectUri: String,
    ): CalendarTokens

    // 날짜 범위로 이벤트 조회
    fun fetchEventsForDateRange(
        connectType: ConnectType,
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Pair<List<Pair<LocalDate, ScheduleContent>>, String?>

    fun fetchEventsWithSyncToken(
        connectType: ConnectType,
        accessToken: String,
        syncToken: String?,
    ): ProviderEventsResponse

    fun registerWatch(
        connectType: ConnectType,
        accessToken: String,
        calendarId: CalendarIdType,
        callbackUrl: String,
    ): GoogleWatchResponse
}
