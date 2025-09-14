package com.day.on.recommend.model

import com.day.on.account.type.System
import com.day.on.recommend.type.RecommendCardStatus
import java.time.LocalDateTime

data class RecommendCard(
    val id: Long,
    val accountId: Long,
    val dailyId: Long,
    val contents: String,
    val startTimeSlot: LocalDateTime,
    val endTimeSlot: LocalDateTime,
    val recommendPlace: RecommendPlace?,
    val status: RecommendCardStatus,
    val createdId: String = System.SYSTEM_ID.id,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedId: String = System.SYSTEM_ID.id,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)