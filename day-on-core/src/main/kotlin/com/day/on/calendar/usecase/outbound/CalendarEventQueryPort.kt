package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.ScheduleContent
import java.time.LocalDate
import java.util.*

interface CalendarEventQueryPort {
    /** DB에서 날짜별 통합 일정 조회 */
    fun findByDate(accountId: Long, date: LocalDate): List<ScheduleContent>
}