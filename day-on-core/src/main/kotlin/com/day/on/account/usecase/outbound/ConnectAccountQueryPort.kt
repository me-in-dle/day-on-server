package com.day.on.account.usecase.outbound

import com.day.on.account.type.ConnectType

interface ConnectAccountQueryPort {
    fun existByEmail(email: String, connectType: ConnectType): Boolean
}