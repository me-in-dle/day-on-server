package com.day.on.calendar.repository

import com.day.on.calendar.jpa.ServiceTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceTokenJpaRepository : JpaRepository<ServiceTokenEntity, Long> {
    fun existsByConnectionId(accountId: Long): Boolean
}