package com.day.on.calendar.adapter

import com.day.on.calendar.repository.CalendarConnectionJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class CalendarConnectionAdapter (private val jpaRepository: CalendarConnectionJpaRepository): CalendarConnectionPort {
    override fun existsByAccountIdAndIsActive(accountId: Long): Boolean {
        return jpaRepository.existsByAccountIdAndIsActive(accountId, true)
    }
}