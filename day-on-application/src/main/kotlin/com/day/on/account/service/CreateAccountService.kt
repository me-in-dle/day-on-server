package com.day.on.account.service

import com.day.on.account.model.Account
import com.day.on.account.model.ConnectAccount
import com.day.on.account.usecase.inbound.CreateAccountUseCase
import com.day.on.account.usecase.outbound.AccountCommandPort
import com.day.on.account.usecase.outbound.AccountQueryPort
import com.day.on.account.usecase.outbound.ConnectAccountCommandPort
import com.day.on.account.usecase.outbound.ConnectAccountQueryPort
import com.day.on.common.lock.DistributedLockBeforeTransactionAnnotation
import com.day.on.common.lock.DistributedLockPrefix
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CreateAccountService(
    private val accountQueryPort: AccountQueryPort,
    private val accountCommandPort: AccountCommandPort,
    private val connectAccountQueryPort: ConnectAccountQueryPort,
    private val connectAccountCommandPort: ConnectAccountCommandPort,
) : CreateAccountUseCase {

    @DistributedLockBeforeTransactionAnnotation(
        key = ["#connectAccount.email", "#connectAccount.connectType.name"],
        prefix = DistributedLockPrefix.MEMBER_REGISTER,
        separator = ":"
    )
    override fun createAccount(connectAccount: ConnectAccount): Account {
        connectAccountQueryPort.findByEmail(connectAccount.email, connectAccount.connectType)?.let {
            return accountQueryPort.findById(it.accountId)
        }

        val account = accountCommandPort.save(
            Account(
                id = 0L,
                nickName = UUID.randomUUID().toString(),
            )
        )

        connectAccountCommandPort.save(
            ConnectAccount(
                id = 0L,
                email = connectAccount.email,
                accountId = account.id,
                connectType = connectAccount.connectType,
                isEmailVerified = connectAccount.isEmailVerified,
            )
        )

        return account
    }
}