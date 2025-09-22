package com.day.on.calendar.repository

import com.day.on.calendar.jpa.DailyScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyScheduleJpaRepository : JpaRepository<DailyScheduleEntity, Long> {
    fun findByAccountIdAndDay(accountId: Long, day: LocalDate): DailyScheduleEntity?

    fun findByAccountIdAndId(accountId: Long, id: Long): DailyScheduleEntity?
}