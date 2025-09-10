package com.day.on.account.model

import com.day.on.account.type.ConnectType
import com.day.on.account.type.System
import java.time.LocalDateTime

data class ConnectAccount(
    val id: Long,
    val email: String,
    val accountId: Long,
    val connectType: ConnectType,
    val isEmailVerified: Boolean,
    val createdId: String = System.SYSTEM_ID.id,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedId: String = System.SYSTEM_ID.id,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)