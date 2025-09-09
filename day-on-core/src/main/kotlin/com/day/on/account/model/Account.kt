package com.day.on.account.model

import java.time.LocalDateTime

data class Account(
    val id: Long,
    val nickName: String,
    val createdId: String,
    val createdAt: LocalDateTime,
    val updatedId: String,
    val updatedAt: LocalDateTime,
    val age: Int? = null,
)