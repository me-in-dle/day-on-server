package com.day.on.account.usecase.outbound

import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType

interface ConnectSocialAccountPort {
    fun connect(code: String, connectType: ConnectType): ConnectAccount
}