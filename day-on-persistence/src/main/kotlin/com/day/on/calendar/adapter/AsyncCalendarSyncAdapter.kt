package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.usecase.outbound.*
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
    private val lockManager: LockManager,
    private val connectionPort: CalendarConnectionPort,
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
        currentDate: LocalDate,
    ) {
        try {
            val checkStart = currentDate.minusDays(4)
            val checkEnd = currentDate.plusDays(4)

            // 1. ±4일 범위에 빠진 날 있는지 확인
            val missingAround = eventQueryPort.findMissingDays(accountId, checkStart, checkEnd)
            if (missingAround.isEmpty()) {
                logger.debug("Prefetch skipped: all days exist for $accountId, range=$checkStart~$checkEnd")
                return
            }

            val syncStart = currentDate
            val syncEnd = currentDate.plusDays(7)

            val lockKey = "calendar:prefetch:$accountId"

            try {
                lockManager.lock(lockKey) {
                    val stillMissing = eventQueryPort.findMissingDays(accountId, syncStart, syncEnd)
                    if (stillMissing.isEmpty()) {
                        logger.debug("No missing days after lock for $accountId")
                        return@lock
                    }

                    val missingStart = stillMissing.minOrNull()!!
                    val missingEnd = stillMissing.maxOrNull()!!

                    // 2. 없는 날 DailySchedule만 생성
                    eventSyncPort.createDailySchedulesForRange(accountId, missingStart, missingEnd)

                    val (events, nextSyncToken) =
                        providerClientPort.fetchEventsForDateRange(
                            connectType,
                            accessToken,
                            missingStart,
                            missingEnd,
                        )

                    // syncToken 갱신
                    if (!nextSyncToken.isNullOrBlank()) {
                        connectionPort.updateSyncTokenByAccountId(accountId, connectType.connectTypeName, nextSyncToken)
                        logger.info("Updated syncToken for accountId=$accountId after prefetch")
                    }

                    if (events.isNotEmpty()) {
                        eventSyncPort.saveEventsForDateRange(accountId, missingStart, missingEnd, events)
                        logger.info("Prefetch completed: accountId=$accountId, events=${events.size}, range=$missingStart~$missingEnd")
                    } else {
                        logger.info("Prefetch completed: no events found for $accountId, range=$missingStart~$missingEnd")
                    }
                }
            } catch (e: IllegalStateException) {
                logger.debug("Prefetch already in progress for $accountId: ${e.message}")
            }
        } catch (e: Exception) {
            logger.error("Prefetch failed for accountId=$accountId, currentDate=$currentDate", e)
        }
    }
}
