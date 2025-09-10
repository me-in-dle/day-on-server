package com.day.on.account.adapter

import com.day.on.account.jpa.repository.AccountJpaEntityRepository
import com.day.on.account.model.Account
import com.day.on.account.usecase.outbound.AccountQueryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class AccountQueryAdapter(
    private val accountJpaEntityRepository: AccountJpaEntityRepository,
) : AccountQueryPort {
    override fun findById(id: Long): Account {
        return accountJpaEntityRepository.findAccountJpaEntitiesById(id)?.toDomainModel()
            ?: throw IllegalArgumentException("Account not found with id: $id")
    }
}