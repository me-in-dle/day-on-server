package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.CalendarTokens

interface CalendarTokenPort {
    // 토큰 저장/조회
    fun save(token: CalendarTokens)
    fun findByAccountIdAndService(accountId: Long, service: String): CalendarTokens?
}