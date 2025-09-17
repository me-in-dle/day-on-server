package com.day.on.common.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime

data class CalendarTokens(
// TODO : 수정필요 -> 패키지 이동
        val accountId: Long,
        val connectType: ConnectType,
        val accessToken: String,
        val refreshToken: String,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now()
)
