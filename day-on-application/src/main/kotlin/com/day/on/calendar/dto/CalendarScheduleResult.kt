package com.day.on.calendar.dto

import com.day.on.calendar.model.ScheduleContent

data class CalendarScheduleResult(
    val isConnected: Boolean,
    val scheduleContents: List<ScheduleContent>,
)
