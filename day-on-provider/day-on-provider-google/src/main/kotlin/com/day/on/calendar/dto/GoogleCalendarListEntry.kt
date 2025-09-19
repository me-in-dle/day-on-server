package com.day.on.calendar.dto

data class GoogleCalendarListEntry(
    val id: String,
    val summary: String?,
    val timeZone: String?,
    val primary: Boolean? = false
)
