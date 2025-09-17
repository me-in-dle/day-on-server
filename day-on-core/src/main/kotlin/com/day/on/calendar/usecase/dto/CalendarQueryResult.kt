package com.day.on.calendar.usecase.dto

import com.day.on.calendar.model.ScheduleContent

sealed interface CalendarQueryResult {
    data object NotConnected : CalendarQueryResult
    data class Connected(val schedules: List<ScheduleContent>) : CalendarQueryResult
}