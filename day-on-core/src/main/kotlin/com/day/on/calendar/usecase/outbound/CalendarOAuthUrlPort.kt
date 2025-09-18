package com.day.on.calendar.usecase.outbound

interface CalendarOAuthUrlPort {
    fun createCalendarAuthUrl(): String
}