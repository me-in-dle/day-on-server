package com.day.on.account.jpa.repository

import com.day.on.account.jpa.entity.ConnectAccountJpaEntity
import com.day.on.account.type.ConnectType
import org.springframework.data.jpa.repository.JpaRepository

interface ConnectAccountJpaEntityRepository : JpaRepository<ConnectAccountJpaEntity, Long> {
    fun findByEmailAndConnectType(email: String, connectType: ConnectType): ConnectAccountJpaEntity?
}