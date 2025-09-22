package com.day.on.recommend.adapter

import com.day.on.recommend.jpa.repository.RecommendFeedbackJpaEntityRepository
import com.day.on.recommend.jpa.repository.query.RecommendFeedbackNativeRepository
import com.day.on.recommend.model.RecommendFeedback
import com.day.on.recommend.type.RecommendTimeSlot
import com.day.on.recommend.usecase.outbound.RecommendFeedbackQueryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class RecommendFeedbackQueryAdapter(
    private val recommendFeedbackJpaRepository: RecommendFeedbackJpaEntityRepository,
    private val recommendFeedbackNativeRepository: RecommendFeedbackNativeRepository,
) : RecommendFeedbackQueryPort {
    override fun findRecommendFeedbackPlaceLTypeBy(
        accountId: Long,
        emptyActiveTimes: List<RecommendTimeSlot>
    ): List<RecommendFeedback> {
        return recommendFeedbackNativeRepository.findRecommendFeedbackPlaceLTypeBy(
            accountId = accountId,
            emptyActiveTimes = emptyActiveTimes
        ).map { it.toDomainModel() }
    }

}