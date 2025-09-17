package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent
import java.time.LocalDate
import java.util.*

interface CalendarCachePort {
    fun get(accountId: Long, date: LocalDate): List<ScheduleContent>?
    fun put(accountId: Long, date: LocalDate, schedules: List<ScheduleContent>, ttlSeconds: Long)
}