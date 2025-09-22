package com.day.on.recommend.service

import com.day.on.calendar.usecase.outbound.ScheduleContentQueryPort
import com.day.on.recommend.usecase.inbound.CreateRecommendCardUseCase
import com.day.on.recommend.usecase.outbound.RecommendCardQueryPort
import java.time.LocalTime

class CreateRecommendCardService(
    private val recommendCardQueryPort: RecommendCardQueryPort,
    private val scheduleContentQueryPort: ScheduleContentQueryPort,

) : CreateRecommendCardUseCase {
    override fun create(accountId: Long, dailyId: Long) {
        // 1. 캘린더 컨텐츠 조회
        val scheduleContents = scheduleContentQueryPort.findScheduleContentBy(accountId, dailyId)

        // 2. 비어있는 시간 영역 조회 (9시 ~ 22시)
        val emptyTimeZonesToActive: MutableList<Pair<LocalTime, LocalTime>> = mutableListOf()
        var endActiveTime: LocalTime = LocalTime.now().withHour(9).withMinute(0)
        scheduleContents.forEach { scheduleContent ->
            if (scheduleContent.startTime.isAfter(endActiveTime)) {
                emptyTimeZonesToActive.add(endActiveTime to scheduleContent.startTime)
                endActiveTime = scheduleContent.startTime
            }
        }

        // 3. 최근 1달 비어있는 시간에 매칭되는 시간대별 상위 선호도 2개씩 데이터 조회 내일 반영


        // 4. 비어있는 시간 영역 place 기반 추천카드 마스터 생성 (피드백 선호2개, 피드백 비선호1, 랜덤 2)
    }

}