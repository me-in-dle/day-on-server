package com.day.on.account.adapter

import com.day.on.account.jpa.entity.AccountJpaEntity
import com.day.on.account.jpa.repository.AccountJpaEntityRepository
import com.day.on.account.model.Account
import com.day.on.account.usecase.outbound.AccountCommandPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = false)
class AccountCommandAdapter(
    private val accountJpaEntityRepository: AccountJpaEntityRepository,
) : AccountCommandPort {
    override fun save(account: Account): Account {
        return accountJpaEntityRepository.save(AccountJpaEntity.from(account)).toDomainModel()
    }
}