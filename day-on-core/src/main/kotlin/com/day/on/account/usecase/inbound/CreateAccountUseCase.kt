package com.day.on.account.usecase.inbound

import com.day.on.account.model.Account
import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType

interface CreateAccountUseCase {
    fun createAccount(connectAccount: ConnectAccount): Account
}