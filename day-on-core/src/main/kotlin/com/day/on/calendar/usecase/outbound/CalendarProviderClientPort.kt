package com.day.on.calendar.usecase.outbound

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.CalendarTokens
import java.time.LocalDate

interface CalendarProviderClientPort {
    // 외부 캘린더 호출 코드→토큰 교환 및 이벤트 조회 포트
    fun exchangeCodeForToken(connectType: ConnectType, code: String, redirectUri: String): CalendarTokens

    // 날짜 범위로 이벤트 조회
    fun fetchEventsForDateRange(
            connectType: ConnectType,
            accessToken: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): List<Pair<LocalDate, ScheduleContent>>
}