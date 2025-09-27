package com.day.on.recommend.usecase.outbound

import com.day.on.recommend.model.RecommendFeedback
import com.day.on.recommend.type.RecommendTimeSlot

interface RecommendFeedbackQueryPort {
    fun findRecommendFeedbackPlaceLTypeBy(
        accountId: Long,
        emptyActiveTimes: List<RecommendTimeSlot>
    ): List<RecommendFeedback>
}