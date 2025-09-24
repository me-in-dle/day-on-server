package com.day.on.calendar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleCalendarEventsResponse(
    val kind: String?,
    val etag: String?,
    val summary: String?,
    @JsonProperty("nextPageToken")
    val nextPageToken: String?,
    @JsonProperty("nextSyncToken")
    val nextSyncToken: String?,
    val items: List<GoogleCalendarEvent> = emptyList(),
)
