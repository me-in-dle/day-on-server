package com.day.on.calendar.service

import com.day.on.calendar.usecase.dto.CalendarQueryResult
import com.day.on.calendar.usecase.inbound.CalendarQueryUseCase
import com.day.on.calendar.usecase.outbound.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

/**
 * ✅ CalendarQueryService
 *
 * - 메인 진입/날짜 선택 시 사용자의 일정 조회 흐름을 담당
 *
 * [정책 요약]
 * 1. 연동 여부 확인
 *    - CalendarConnection이 없으면 → NotConnected 반환
 *    - 클라이언트는 isConnected=false를 보고 화면 표시
 *
 * 2. 오늘 날짜 요청 (캐싱 전략 적용)
 *    - Redis 조회 (빠른 응답)
 *    - 캐시 HIT → 바로 반환
 *    - 캐시 MISS → DB 조회 → Redis에 TTL=1시간으로 저장
 *
 * 3. 다른 날짜 요청
 *    - Redis는 사용 ❌ (오늘만 캐싱)
 *    - 이후 daily schedules day 범위 확인 후 -> 없으면 미리 비동기로 가져옴
 *    - 바로 DB 조회
 *
 * [추가]
 * - 웹훅에서 DB를 최신화
 */
@Service
class CalendarQueryService(
        private val connectionPort: CalendarConnectionPort,
        private val cachePort: CalendarCachePort,
        private val eventQueryPort: CalendarEventQueryPort,
        private val tokenPort: CalendarTokenPort,
        private val asyncSyncPort: AsyncCalendarSyncPort
) : CalendarQueryUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)
    /**
     * 1. 최초연동: 선택한 날을 기준으로 (외부api호출해서 일주일치 동기화) ex 1-7일치가져옴
     * 2. 사용자가 4일 선택: DB에 있음 → 즉시 응답
     * 3. 백그라운드 체크: 4+7=8일까지 DB 확인 → 8일 데이터 없음
     * 4. 비동기로 8-14일 동기화
     */
    override fun getByDate(accountId: Long, date: LocalDate): CalendarQueryResult {
        val connection = connectionPort.findByAccountId(accountId)
        val today = LocalDate.now()
        // TODO : 사용자가 외부 캘린더를 구별하기 위해 추후 파람으로 connectType함께 확인후 findByDate
        // 2) 당일 여부 조회
        val schedules = try {
            if (date == today) {
                // 오늘이면 캐시 우선
                cachePort.get(accountId, date)
                        ?: eventQueryPort.findByDate(accountId, date).also {
                            if (it.isNotEmpty()) {
                                cachePort.put(accountId, date, it, ttlSeconds = 3600)
                            }
                        }
            } else {
                // 다른 날짜는 DB 조회
                eventQueryPort.findByDate(accountId, date)
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch schedules for accountId=$accountId, date=$date", e)
            emptyList()
        }

        // 외부 연동되어 있고, daily schedules에 범위 체크 후 백그라운드 실시간 동기화
        if (connection?.isActive == true) {
            val token = tokenPort.findByAccountIdAndConnectType(accountId, connection.provider.connectTypeName)

            if (token != null) {
                try {
                    // 현재 날짜 기준 앞뒤로 +- 4일 체크 및 Prefetch
                    asyncSyncPort.prefetchIfNeeded(
                            accountId,
                            connection.provider,
                            token.accessToken,
                            date
                    )
                } catch (e: Exception) {
                    logger.warn("Prefetch scheduling failed", e)
                    // 실패해도 현재 데이터는 반환
                }
            }
        }
        // 2) 연동 여부 판단 (연동 정보는 schedules 조회와 별개)
        return if (connection?.isActive == true) {
            CalendarQueryResult.Connected(schedules = schedules, connectType = connection.provider)
        } else {
            CalendarQueryResult.NotConnected(schedules = schedules)
        }

    }


}
