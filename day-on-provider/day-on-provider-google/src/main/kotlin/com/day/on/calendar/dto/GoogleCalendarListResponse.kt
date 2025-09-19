package com.day.on.calendar.dto

data class GoogleCalendarListResponse(
        val items: List<GoogleCalendarListEntry> = emptyList()
)
