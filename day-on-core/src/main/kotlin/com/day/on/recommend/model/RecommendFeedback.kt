package com.day.on.recommend.model

import com.day.on.account.type.System
import com.day.on.recommend.type.FeedbackAction
import com.day.on.recommend.type.RecommendTimeSlot
import java.time.LocalDateTime

data class RecommendFeedback(
    val id: Long,
    val accountId: Long,
    val recommendPlace: RecommendPlace,
    val timeSlot: RecommendTimeSlot,
    val mismatchedCount: Long,
    val matchedCount: Long,
    val feedbackActions: List<FeedbackAction>,
    val lastMismatchedTime: LocalDateTime?,
    val lastMatchedTime: LocalDateTime?,
    val createdId: String = System.SYSTEM_ID.id,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedId: String = System.SYSTEM_ID.id,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)