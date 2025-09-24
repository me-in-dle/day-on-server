package com.day.on.calendar.repository

import com.day.on.calendar.jpa.DailyScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyScheduleJpaRepository : JpaRepository<DailyScheduleEntity, Long> {
    fun findByAccountIdAndDay(
        accountId: Long,
        day: LocalDate,
    ): DailyScheduleEntity?

    fun findByAccountIdAndDayIn(
        accountId: Long,
        days: Collection<LocalDate>,
    ): List<DailyScheduleEntity>

    // 날짜 범위 조회
    fun findByAccountIdAndDayBetween(
        accountId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<DailyScheduleEntity>

}

