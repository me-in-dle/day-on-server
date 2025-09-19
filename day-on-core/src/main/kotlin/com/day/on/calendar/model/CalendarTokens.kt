package com.day.on.calendar.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime

data class CalendarTokens(
    val accountId: Long,
    val connectType: ConnectType,
    val accessToken: String,
    val refreshToken: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
