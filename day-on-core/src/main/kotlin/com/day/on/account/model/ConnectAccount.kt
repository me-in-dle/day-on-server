package com.day.on.account.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime

data class ConnectAccount(
    val id: Long,
    val email: String,
    val accountId: Long,
    val connectType: ConnectType,
    val isEmailVerified: Boolean,
    val createdId: String,
    val createdAt: LocalDateTime,
    val updatedId: String,
    val updatedAt: LocalDateTime,
)