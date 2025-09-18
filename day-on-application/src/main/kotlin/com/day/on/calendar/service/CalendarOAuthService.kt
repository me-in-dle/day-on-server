package com.day.on.calendar.service

import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase
import com.day.on.calendar.usecase.outbound.CalendarOAuthUrlPort
import org.springframework.stereotype.Service

@Service
class CalendarOAuthService (private val calendarOAuthUrlPort: CalendarOAuthUrlPort) : CalendarOAuthUseCase{
    override fun generateCalendarOAuthUrl(accountId: Long): String {
        return calendarOAuthUrlPort.createCalendarAuthUrl()
    }

}