package com.day.on.account.adapter

import com.day.on.account.jpa.repository.ConnectAccountJpaEntityRepository
import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.outbound.ConnectAccountQueryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ConnectAccountQueryAdapter(
    private val connectAccountJpaEntityRepository: ConnectAccountJpaEntityRepository,
) : ConnectAccountQueryPort {
    override fun findByEmail(email: String, connectType: ConnectType): ConnectAccount? {
        return connectAccountJpaEntityRepository.findByEmailAndConnectType(email, connectType)?.toDomainModel()
    }
}