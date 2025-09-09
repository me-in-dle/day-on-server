package com.day.on.account.usecase.outbound

import com.day.on.account.model.Account

interface AccountCommandPort {
    fun save(account: Account): Account
}