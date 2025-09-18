package com.day.on.calendar.usecase.dto

import com.day.on.calendar.model.ScheduleContent

sealed interface CalendarQueryResult {
    data class NotConnected(val schedules: List<ScheduleContent>) : CalendarQueryResult
    data class Connected(
            val schedules: List<ScheduleContent>,
            val connectType: com.day.on.account.type.ConnectType? = null
    ) : CalendarQueryResult
}