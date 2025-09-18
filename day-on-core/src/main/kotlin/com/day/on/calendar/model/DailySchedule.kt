package com.day.on.calendar.model

import java.time.LocalDate
import java.time.LocalDateTime

data class DailySchedule(
        val id: Long,
        val accountId: Long,
        val day: LocalDate,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now(), )
