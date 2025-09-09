package com.day.on.account.usecase.inbound

import com.day.on.account.model.Account
import com.day.on.account.type.ConnectType

interface CreateAccountUseCase {
    fun createAccount(accountId: Long?, code: String, connectType: ConnectType): Account
}