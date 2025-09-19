package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.CalendarConnectionEntity
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.repository.CalendarConnectionJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class CalendarConnectionAdapter (private val jpaRepository: CalendarConnectionJpaRepository): CalendarConnectionPort {
    override fun existsByAccountIdAndIsActive(accountId: Long): Boolean {
        return jpaRepository.existsByAccountIdAndIsActive(accountId, true)
    }
    override fun findByAccountId(accountId: Long): CalendarConnection? {
        return jpaRepository.findByAccountId(accountId)?.toDomain()
    }

    @Transactional(readOnly = false)
    override fun save(connection: CalendarConnection): CalendarConnection {
        val entity = CalendarConnectionEntity.fromDomain(connection)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }
}