package com.day.on.recommend.usecase.inbound

interface CreateRecommendCardUseCase {
    fun create(accountId: Long, dailyId: Long)
}