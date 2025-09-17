package com.day.on.calendar.usecase.inbound

import com.day.on.calendar.usecase.dto.CalendarQueryResult
import java.time.LocalDate
import java.util.*

interface CalendarQueryUseCase {
    fun getByDate(accountId: Long, date: LocalDate): CalendarQueryResult
}