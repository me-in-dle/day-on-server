package com.day.on.account.usecase.outbound

import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType

interface ConnectAccountQueryPort {
    fun findByEmail(email: String, connectType: ConnectType): ConnectAccount?
}