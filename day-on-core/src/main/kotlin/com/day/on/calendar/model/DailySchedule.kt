package com.day.on.calendar.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class DailySchedule(

    val id: Long,
    val accountId: Long,
    val day: Date,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),

)
