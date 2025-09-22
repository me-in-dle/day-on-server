package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.DailySchedule

interface DailyScheduleQueryPort {
    fun findBy(dailyId: Long, accountId: Long): DailySchedule
}