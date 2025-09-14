package com.day.on.recommend.usecase.outbound

import com.day.on.recommend.model.RecommendCard

interface UpdateContentsRecommendCardPort {
    fun updateContents(recommendCards: List<RecommendCard>): List<RecommendCard>
}