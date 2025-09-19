package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent

interface CalendarEventSyncPort {
    fun saveMonthly(accountId: Long, year: Int, month: Int, events: List<ScheduleContent>)
}