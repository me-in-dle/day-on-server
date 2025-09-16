package com.day.on.common.model

import java.time.LocalDateTime

data class ServiceTokens(
// TODO : 수정필요 -> 패키지 이동
        val accountId: Long,
        val serviceType: ServiceType,
        val accessToken: String,
        val refreshToken: String?,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now()
)
