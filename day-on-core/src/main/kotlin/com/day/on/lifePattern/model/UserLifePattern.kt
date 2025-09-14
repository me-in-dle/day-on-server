package com.day.on.lifePattern.model

import java.time.LocalDateTime

data class UserLifePattern(
    val id: Long,
    val userId: Long,
    val patternType: String,
    val patternValue: String,
    val updatedAt: LocalDateTime
)
