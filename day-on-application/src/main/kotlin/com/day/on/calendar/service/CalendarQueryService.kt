package com.day.on.calendar.service

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.ScheduleContent
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
    private val calendarProviderClientPort: CalendarProviderClientPort,
    private val prefetchSyncPort: PrefetchCalendarSyncPort,
) : CalendarQueryUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 1. 최초연동: 선택한 날을 기준으로 (외부api호출해서 일주일치 동기화) ex 1-7일치가져옴
     * 2. 사용자가 4일 선택: DB에 있음 → 즉시 응답
     * 3. 백그라운드 체크: 4+7=8일까지 DB 확인 → 8일 데이터 없음
     * 4. 8-14일 동기화
     */
    override fun getByDate(
        accountId: Long,
        date: LocalDate,
    ): CalendarQueryResult {
        // 1. 연동 정보 확인
        val connection = connectionPort.findByAccountId(accountId)

        // 2. 연동되어 있으면 먼저 동기화 체크 및 실행
        if (connection?.isActive == true) {
            syncIfNeeded(accountId, connection.provider, date)
        }

        // 3. 데이터 조회 (동기화 완료 후)
        val schedules = fetchSchedules(accountId, date)

        // 4. 결과 반환
        return if (connection?.isActive == true) {
            CalendarQueryResult.Connected(
                schedules = schedules,
                connectType = connection.provider
            )
        } else {
            CalendarQueryResult.NotConnected(schedules = schedules)
        }
    }

    /**
     * 동기화가 필요한지 체크하고, 필요하면 동기 실행
     */
    private fun syncIfNeeded(accountId: Long, provider: ConnectType, date: LocalDate) {
        try {
            // 토큰 조회 및 갱신
            var token = tokenPort.findByAccountIdAndConnectType(accountId, provider)

            if (token == null) {
                logger.warn("No token found for accountId=$accountId, provider=$provider")
                return
            }

            // 토큰 만료 시 갱신
            if (token.isExpired()) {
                token = try {
                    calendarProviderClientPort.getRefreshToken(
                        accountId,
                        provider,
                        token.refreshToken
                    ).also {
                        logger.info("Token refreshed for accountId=$accountId")
                    }
                } catch (e: Exception) {
                    logger.error("Failed to refresh token for accountId=$accountId", e)
                    return
                }
            }

            // Prefetch 동기 실행
            if (token != null) {
                prefetchSyncPort.prefetchIfNeeded(
                    accountId,
                    provider,
                    token.accessToken,
                    date,
                )
            }
        } catch (e: Exception) {
            logger.error("Sync failed for accountId=$accountId, date=$date", e)
            // 실패해도 기존 DB 데이터는 반환되도록
        }
    }

    /**
     * 스케줄 데이터 조회
     */
    private fun fetchSchedules(accountId: Long, date: LocalDate): List<ScheduleContent> {
        val today = LocalDate.now()

        return try {
            if (date == today) {
                // 오늘: 캐시 우선 조회
                cachePort.get(accountId, date) ?: run {
                    eventQueryPort.findByDate(accountId, date).also { schedules ->
                        if (schedules.isNotEmpty()) {
                            cachePort.put(accountId, date, schedules, ttlSeconds = 3600)
                        }
                    }
                }
            } else {
                // 다른 날짜: DB 조회
                eventQueryPort.findByDate(accountId, date)
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch schedules for accountId=$accountId, date=$date", e)
            emptyList()
        }
    }
}
