package com.day.on.calendar.service

import com.day.on.calendar.usecase.dto.CalendarQueryResult
import com.day.on.calendar.usecase.inbound.CalendarQueryUseCase
import com.day.on.calendar.usecase.outbound.CalendarCachePort
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import com.day.on.calendar.usecase.outbound.CalendarEventQueryPort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * ✅ CalendarQueryService
 *
 * - 메인 진입/날짜 선택 시 사용자의 일정 조회 흐름을 담당
 *
 * [정책 요약]
 * 1. 연동 여부 확인
 *    - CalendarConnection이 없으면 → NotConnected 반환
 *    - 클라이언트는 isConnected=false를 보고 "캘린더 연동할까요?" 모달 표시
 *
 * lastSynced 확인
 * 만약 월이 바뀜 → 이번 달 전체 동기화 다시 실행 (구글 API 호출 → DB 저장)
 * 2. 오늘 날짜 요청 (캐싱 전략 적용)
 *    - Redis 조회 (빠른 응답)
 *    - 캐시 HIT → 바로 반환
 *    - 캐시 MISS → DB 조회 → Redis에 TTL=1시간으로 저장
 *
 * 3. 다른 날짜 요청
 *    - Redis는 사용 ❌ (오늘만 캐싱)
 *    - 바로 DB 조회
 *
 * [추가]
 * - isConnected=true 받은 클라이언트는 이후부터 polling을 시작
 * - 웹훅에서 DB를 최신화
 */
@Service
class CalendarQueryService(
        private val connectionPort: CalendarConnectionPort,
        private val cachePort: CalendarCachePort,
        private val eventQueryPort: CalendarEventQueryPort
) : CalendarQueryUseCase {

    override fun getByDate(accountId: Long, date: LocalDate): CalendarQueryResult {
        // 1) 연동 여부 판단
        if (!connectionPort.existsByAccountIdAndIsActive(accountId)) {
            return CalendarQueryResult.NotConnected
        }

        // 1-1) TODO : lastSynced 확인
        // 만약 월이 바뀜 → 이번 달 전체 동기화 다시 실행 (구글 API 호출 → DB 저장(당일이면 redis캐시))


        // 2) 당일 여부 조회
        val today = LocalDate.now()
        val schedules = if (date == today) { // 당일이면 Redis 조회
            cachePort.get(accountId, date)
                    ?: eventQueryPort.findByDate(accountId, date).also {
                        // 캐시 MISS → DB 조회 후 Redis 캐싱 (TTL 1시간)
                        cachePort.put(accountId, date, it, ttlSeconds = 3600)
                    }
        } else {
            // 당일아니면 DB 조회
            eventQueryPort.findByDate(accountId, date)
        }

        return CalendarQueryResult.Connected(schedules)
    }


}
