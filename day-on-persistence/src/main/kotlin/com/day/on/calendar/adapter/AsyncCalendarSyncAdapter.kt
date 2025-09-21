package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.usecase.outbound.AsyncCalendarSyncPort
import com.day.on.calendar.usecase.outbound.CalendarEventQueryPort
import com.day.on.calendar.usecase.outbound.CalendarEventSyncPort
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import com.day.on.common.outbound.LockManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AsyncCalendarSyncAdapter(
        private val providerClientPort: CalendarProviderClientPort,
        private val eventSyncPort: CalendarEventSyncPort,
        private val eventQueryPort: CalendarEventQueryPort,
        private val lockManager: LockManager
) : AsyncCalendarSyncPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Prefetch: 현재 날짜 ±4일 범위 체크 후 없으면 동기화
     */
    @Async("asyncTaskExecutor")
    override fun prefetchIfNeeded(
            accountId: Long,
            connectType: ConnectType,
            accessToken: String,
            currentDate: LocalDate
    ) {
        try {
            val start = currentDate.minusDays(4)
            val end = currentDate.plusDays(4)

            // 1. DailySchedules 범위 존재 여부 확인
            val hasMissing = eventQueryPort.hasMissingDailySchedules(accountId, start, end)


            if (!hasMissing) {
                logger.debug("Prefetch skipped: DailySchedules already exist for accountId=$accountId, range=$start~$end")
                return
            }

            logger.info("Prefetch needed for accountId=$accountId, range=$start~$end")

            // 2. 분산락으로 중복 동기화 방지
            val lockKey = "calendar:prefetch:$accountId"

            try {
                lockManager.lock(lockKey) {
                    // double-check
                    val stillNeedsSync = eventQueryPort.hasMissingDailySchedules(accountId, start, end)

                    if (!stillNeedsSync) {
                        logger.debug("DailySchedules already created by another process")
                        return@lock
                    }

                    val syncStart = currentDate
                    val syncEnd   = currentDate.plusDays(7)

                    // DB에 저장 (DailySchedules 생성 + 이벤트 저장)
                    eventSyncPort.createDailySchedulesForRange(accountId, syncStart, syncEnd)

                    // 3. 외부에서 데이터 가져오기
                    val events = providerClientPort.fetchEventsForDateRange(
                            connectType,
                            accessToken,
                            syncStart,
                            syncEnd
                    )

                    if (events.isNotEmpty()) {
                        eventSyncPort.saveEventsForDateRange(accountId, syncStart, syncEnd, events)
                        logger.info("Prefetch completed: accountId=$accountId, events=${events.size}")
                    } else {
                        logger.info("Prefetch completed: no events found for accountId=$accountId")
                    }
                }
            } catch (e: IllegalStateException) {
                logger.debug("Prefetch already in progress for accountId=$accountId: ${e.message}")
            }

        } catch (e: Exception) {
            logger.error("Prefetch failed for accountId=$accountId, currentDate=$currentDate", e)
        }
    }
}
