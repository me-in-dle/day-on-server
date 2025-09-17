package com.day.on.calendar.dto

data class CalendarResponse(
    val isConnected: Boolean,
    val schedules: List<ScheduleContentResponse>
)
