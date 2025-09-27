package com.day.on.calendar.usecase.dto

import com.day.on.calendar.model.ScheduleContent
import java.time.LocalDate

data class ProviderEventChange(
    val externalEventId: String,
    val status: String?, // confirmed / cancelled / tentative
    val eventDate: LocalDate,
    val schedule: ScheduleContent?, // 삭제 이벤트면 null
)
