package com.day.on.account.model

import com.day.on.account.type.System
import java.time.LocalDateTime

data class Account(
    val id: Long,
    val nickName: String,
    val createdId: String = System.SYSTEM_ID.id,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedId: String = System.SYSTEM_ID.id,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val age: Int? = null,
)