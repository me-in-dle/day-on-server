package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent

interface ScheduleContentQueryPort {
    fun findScheduleContentBy(accountId: Long, dailyScheduleId: Long): List<ScheduleContent>
}