package com.day.on.calendar.repository

import com.day.on.calendar.jpa.CalendarConnectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarConnectionJpaRepository : JpaRepository<CalendarConnectionEntity, Long> {
    fun existsByAccountIdAndIsActive(accountId: Long, isActive: Boolean = true): Boolean
    fun findByAccountId(accountId: Long): CalendarConnectionEntity?
}