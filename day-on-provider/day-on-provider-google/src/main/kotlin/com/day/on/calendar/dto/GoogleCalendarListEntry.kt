package com.day.on.calendar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleCalendarListEntry(
    val id: String,
    val summary: String?,
    @JsonProperty("timeZone")
    val timeZone: String?,
    val primary: Boolean? = false,
)
