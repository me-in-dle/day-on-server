package com.day.on.calendar.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleContent(
    val id: Long,
    val dailySchedulesId: Long,
    val accountId: Long,
    val externalEventId: String?,
    val relationTypes: ConnectType?,
    val title: String,
    val location: String?,
    val contents: String?,
    val useYn: String = "Y",
    val tagIds: String?,
    val endTime: LocalTime,
    val startTime: LocalTime,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
