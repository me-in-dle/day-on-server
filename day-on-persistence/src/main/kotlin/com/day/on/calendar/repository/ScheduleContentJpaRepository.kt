package com.day.on.calendar.repository

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.ScheduleContentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface ScheduleContentJpaRepository : JpaRepository<ScheduleContentEntity, Long> {
    fun findByDailySchedulesId(dailySchedulesId: Long): List<ScheduleContentEntity>
}