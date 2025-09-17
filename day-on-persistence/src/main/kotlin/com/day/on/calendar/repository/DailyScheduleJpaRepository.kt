package com.day.on.calendar.repository

import com.day.on.calendar.jpa.DailyScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

interface DailyScheduleJpaRepository : JpaRepository<DailyScheduleEntity, Long> {
    fun findByAccountIdAndDay(accountId: Long, day: LocalDate): DailyScheduleEntity?

}