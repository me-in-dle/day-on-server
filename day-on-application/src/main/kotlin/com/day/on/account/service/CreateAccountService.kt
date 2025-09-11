package com.day.on.account.service

import com.day.on.account.model.Account
import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.inbound.CreateAccountUseCase
import com.day.on.account.usecase.outbound.AccountCommandPort
import com.day.on.account.usecase.outbound.AccountQueryPort
import com.day.on.account.usecase.outbound.ConnectAccountCommandPort
import com.day.on.account.usecase.outbound.ConnectAccountQueryPort
import com.day.on.account.usecase.outbound.ConnectSocialAccountPort
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
    private val connectSocialAccountPort: ConnectSocialAccountPort,
    private val connectAccountCommandPort: ConnectAccountCommandPort,
) : CreateAccountUseCase {

    @DistributedLockBeforeTransactionAnnotation(
        key = ["#connectType.name", "#code"],
        prefix = DistributedLockPrefix.MEMBER_REGISTER,
        separator = ":"
    )
    override fun createAccount(accountId: Long?, code: String, connectType: ConnectType): Account {
        val socialAccount = connectSocialAccountPort.connect(code, connectType)
        require(!connectAccountQueryPort.existByEmail(socialAccount.email, connectType)) { "이미 가입된 이메일입니다." }

        accountId?.let {
            connectAccountCommandPort.save(
                ConnectAccount(
                    id = 0L,
                    email = socialAccount.email,
                    accountId = it,
                    connectType = connectType,
                    isEmailVerified = socialAccount.isEmailVerified,
                )
            )

            return accountQueryPort.findById(it)
        } ?: run {
            val account = accountCommandPort.save(
                Account(
                    id = 0L,
                    nickName = UUID.randomUUID().toString(),
                )
            )

            connectAccountCommandPort.save(
                ConnectAccount(
                    id = 0L,
                    email = socialAccount.email,
                    accountId = account.id,
                    connectType = connectType,
                    isEmailVerified = socialAccount.isEmailVerified,
                )
            )

            return account
        }
    }
}