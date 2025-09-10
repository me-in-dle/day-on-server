package com.day.on.account.jpa.repository

import com.day.on.account.jpa.entity.AccountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaEntityRepository : JpaRepository<AccountJpaEntity, Long> {
    fun findAccountJpaEntitiesById(id: Long): AccountJpaEntity?
}