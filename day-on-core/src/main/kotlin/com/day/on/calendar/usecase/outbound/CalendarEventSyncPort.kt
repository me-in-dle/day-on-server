package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent
import java.time.LocalDate

interface CalendarEventSyncPort {
    fun saveEventsForDateRange(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate,
            events: List<Pair<LocalDate, ScheduleContent>>
    )

    // 내부 캘린더 저장
    fun saveInternalEvent(accountId: Long, event: ScheduleContent)

    fun createDailySchedulesForRange(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate
    )
}