package com.day.on.calendar.adapter

import com.day.on.calendar.jpa.DailyScheduleEntity
import com.day.on.calendar.jpa.ScheduleContentEntity
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.repository.DailyScheduleJpaRepository
import com.day.on.calendar.repository.ScheduleContentJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarCachePort
import com.day.on.calendar.usecase.outbound.CalendarEventSyncPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class CalendarEventSyncAdapter(
        private val dailyRepo: DailyScheduleJpaRepository,
        private val contentRepo: ScheduleContentJpaRepository,
        private val cachePort: CalendarCachePort
) : CalendarEventSyncPort {

    private val logger = LoggerFactory.getLogger(javaClass)
    @Transactional(readOnly = false)
    override fun saveMonthly(accountId: Long, year: Int, month: Int, events: List<ScheduleContent>) {
        // events를 day(LocalDate) 기준으로 그룹화
        val byDay = events.groupBy { it.createdAt.toLocalDate() }

        byDay.forEach { (day, listOfEvents) ->
            // daily 생성 또는 조회
            val daily = dailyRepo.findByAccountIdAndDay(accountId, day) ?: dailyRepo.save(
                    DailyScheduleEntity( id = 0L, accountId = accountId, day = day, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now() )
            )

            // 기존 콘텐츠 정책 정하기
            // TODO : upsert 방식으로 수정하기
            val existing = contentRepo.findByDailySchedulesId(daily.id)
            if (existing.isNotEmpty()) contentRepo.deleteAll(existing)

            val entities = listOfEvents.map { sc ->
                ScheduleContentEntity(
                        id = 0L,
                        dailySchedulesId = daily.id,
                        accountId = accountId,
                        relationTypes = sc.relationTypes,
                        title = sc.title,
                        location = sc.location,
                        contents = sc.contents,
                        useYn = sc.useYn,
                        tagIds = sc.tagIds,
                        startTime = sc.startTime,
                        endTime = sc.endTime,
                        status = sc.status,
                        createdAt = sc.createdAt,
                        updatedAt = sc.updatedAt
                )
            }
            contentRepo.saveAll(entities)

            // 오늘이면 캐시 갱신
            if (day == LocalDate.now()) {
                tryUpdateCache(accountId, day, daily.id)
            }
        }
    }

    private fun tryUpdateCache(accountId: Long, day: LocalDate, dailyId: Long) {
        try {
            val saved = contentRepo.findByDailySchedulesId(dailyId).map { it.toDomain() }
            cachePort.put(accountId, day, saved, ttlSeconds = 3600)
        } catch (ex: Exception) {
            // 캐시 실패는 로그만 남기고 트랜잭션은 유지
            logger.warn("Failed to update cache for accountId=$accountId, day=$day", ex)
        }
    }
}