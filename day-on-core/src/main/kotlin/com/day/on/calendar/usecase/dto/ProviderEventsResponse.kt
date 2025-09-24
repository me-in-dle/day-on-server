package com.day.on.calendar.usecase.dto

data class ProviderEventsResponse(
    val events: List<ProviderEventChange>,
    val nextSyncToken: String?,
)
