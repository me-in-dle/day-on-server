package com.day.on.account.adapter

import com.day.on.account.jpa.entity.ConnectAccountJpaEntity
import com.day.on.account.jpa.repository.ConnectAccountJpaEntityRepository
import com.day.on.account.model.ConnectAccount
import com.day.on.account.usecase.outbound.ConnectAccountCommandPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = false)
class ConnectAccountCommandAdapter(
    private val connectAccountJpaEntityRepository: ConnectAccountJpaEntityRepository,
) : ConnectAccountCommandPort {
    override fun save(connectAccount: ConnectAccount): ConnectAccount {
        return connectAccountJpaEntityRepository.save(ConnectAccountJpaEntity.from(connectAccount)).toDomainModel()
    }
}