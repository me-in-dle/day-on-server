package com.day.on.recommend.usecase.inbound

import com.day.on.recommend.model.RecommendCard
import com.day.on.recommend.usecase.inbound.dto.CreateRecommendCardDto

interface CreateRecommendCardUseCase {
    fun create(dto: CreateRecommendCardDto): List<RecommendCard>
}