package com.day.on.calendar.dto

data class GoogleCalendarEvent(
    val id: String?,
    val status: String?,
    val summary: String?,
    val description: String?,
    val start: Map<String, String>?, // { "date": "...", "dateTime": "..." }
    val end: Map<String, String>?,
    val location: String?,
)
