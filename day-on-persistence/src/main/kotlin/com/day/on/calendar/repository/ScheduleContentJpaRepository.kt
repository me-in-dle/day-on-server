package com.day.on.calendar.repository

import com.day.on.calendar.jpa.ScheduleContentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScheduleContentJpaRepository : JpaRepository<ScheduleContentEntity, Long>