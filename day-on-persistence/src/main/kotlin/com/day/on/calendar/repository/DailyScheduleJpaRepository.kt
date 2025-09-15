package com.day.on.calendar.repository

import com.day.on.calendar.jpa.DailyScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyScheduleJpaRepository : JpaRepository<DailyScheduleEntity, Long> {
//    fun findByUserIdAndDay(userId: String, day: LocalDate): DailyScheduleEntity?
//    fun findByUserId(userId: String): List<DailyScheduleEntity>
//    fun findByUserIdAndDayBetween(userId: String, startDay: LocalDate, endDay: LocalDate): List<DailyScheduleEntity>
}