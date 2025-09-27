package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.usecase.dto.ProviderEventChange
import java.time.LocalDate

interface CalendarEventSyncPort {
    fun saveEventsForDateRange(
        accountId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        events: List<Pair<LocalDate, ScheduleContent>>,
    )

    // 내부 캘린더 저장
    fun saveInternalEvent(
        accountId: Long,
        event: ScheduleContent,
    )

    fun createDailySchedulesForRange(
        accountId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    )

    fun applyIncrementalChanges(
        accountId: Long,
        events: List<ProviderEventChange>,
    )

    fun deleteByExternalEventId(
        accountId: Long,
        externalEventId: String,
    )
}
