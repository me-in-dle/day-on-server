package com.day.on.account.usecase.outbound

import com.day.on.account.model.ConnectAccount

interface ConnectAccountCommandPort {
    fun save(connectAccount: ConnectAccount): ConnectAccount
}