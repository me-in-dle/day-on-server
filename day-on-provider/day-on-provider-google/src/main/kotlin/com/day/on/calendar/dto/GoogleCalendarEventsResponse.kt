package com.day.on.calendar.dto

data class GoogleCalendarEventsResponse(
    val kind: String?,
    val etag: String?,
    val summary: String?,
    val nextPageToken: String?,
    val items: List<GoogleCalendarEvent> = emptyList()
)

