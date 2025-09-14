package com.day.on.recommend

import com.day.on.recommend.model.RecommendCard
import com.day.on.recommend.usecase.inbound.CreateRecommendCardUseCase
import com.day.on.recommend.usecase.inbound.dto.CreateRecommendCardDto
import com.day.on.recommend.usecase.outbound.UpdateContentsRecommendCardPort

class CreateRecommendCardService(
    private val updateContentsRecommendCardPort: UpdateContentsRecommendCardPort,
) : CreateRecommendCardUseCase {
    override fun create(dto: CreateRecommendCardDto): List<RecommendCard> {
        // 데일리의 어제, 오늘의 일정을 가져온다.
        // 빈 여가시간을 찾는다. (기본 1시간 단위, 개인의 직업 or 특성에 따라 집중 추천 여가 시간을 찾을 수 있도록, 밤 10시 이후는 자제, 점심시간의 간단 산책 등은 고려)
        // 어제의 활동량, 오늘의 날씨, 현재 위치 정보를 가져온다.
        // 맞춤 추천 카드의 최신 1주일 선호도 데이터를 가져온다.
        // 해당 여가시간에 즐길 수 있는 타입을 추천한다.
        // 이때까지는, 시간 업데이트를 하지 않는다.
        // 유저에게 선호 타입 카드 기반하여 llm으로 5개로 placeType을 필터링하여 저장한다.

        return emptyList()
    }

}