package com.day.on.calendar.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleContent(
        val id: Long,
        val dailySchedulesId: Long, // (daily schedule fk)
        val accountId: Long,
        val relationTypes: ConnectType?,
        val title: String,
        val location: String?,
        val contents: String?,
        val useYn: String = "Y",
        val tagIds: String?,

        val startTime: LocalTime,
        val endTime: LocalTime,
        val status: TaskStatus = TaskStatus.PENDING,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
) {

}
