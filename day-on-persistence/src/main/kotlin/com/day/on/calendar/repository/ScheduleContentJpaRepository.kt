package com.day.on.calendar.repository

import com.day.on.calendar.jpa.ScheduleContentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleContentJpaRepository : JpaRepository<ScheduleContentEntity, Long> {
    fun findByDailySchedulesId(dailySchedulesId: Long): List<ScheduleContentEntity>
    fun deleteByAccountIdAndExternalEventId(accountId: Long, externalEventId: String)
    fun deleteByAccountIdAndExternalEventIdIn(accountId: Long, externalEventIds: List<String>)
    fun findByAccountIdAndExternalEventIdIn(accountId: Long, externalEventIds: List<String>): List<ScheduleContentEntity>
}
