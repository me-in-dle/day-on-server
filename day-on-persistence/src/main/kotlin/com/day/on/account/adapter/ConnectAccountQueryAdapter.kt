package com.day.on.account.adapter

import com.day.on.account.jpa.repository.ConnectAccountJpaEntityRepository
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.outbound.ConnectAccountQueryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ConnectAccountQueryAdapter(
    private val connectAccountJpaEntityRepository: ConnectAccountJpaEntityRepository,
) : ConnectAccountQueryPort {
    override fun existByEmail(email: String, connectType: ConnectType): Boolean {
        return connectAccountJpaEntityRepository.findByEmailAndConnectType(email, connectType) != null
    }
}