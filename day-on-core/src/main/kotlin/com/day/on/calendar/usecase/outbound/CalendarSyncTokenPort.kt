package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.CalendarConnection

interface CalendarSyncTokenPort {
    fun findConnectionByResourceId(resourceId: String): CalendarConnection?

    fun updateSyncToken(
        connectionId: Long,
        newSyncToken: String,
    )
}
