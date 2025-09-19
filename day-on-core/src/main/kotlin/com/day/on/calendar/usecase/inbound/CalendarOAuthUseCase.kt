package com.day.on.calendar.usecase.inbound

interface CalendarOAuthUseCase {
    fun generateCalendarOAuthUrl(accountId: Long, provider: String, forwardUrl : String?): String
    fun handleCallbackAndGetClientRedirect(connectType: String, code: String, state: String?): String
}