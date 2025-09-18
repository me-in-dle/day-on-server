package com.day.on.calendar.usecase.inbound

interface CalendarOAuthUseCase {
    fun generateCalendarOAuthUrl(accountId: Long): String
}