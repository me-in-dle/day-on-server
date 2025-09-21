package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
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
    // 이벤트 저장
    /**
     * 날짜 범위 명시 - 빈 날짜도 DailySchedule 생성
     */
    @Transactional
    override fun saveEventsForDateRange(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate,
            events: List<Pair<LocalDate, ScheduleContent>>
    ) {
        if (events.isEmpty()) {
            logger.info("No events to save for $startDate~$endDate")
            return
        }

        val dates = generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(endDate) }
                .toList()

        val dailyMap = dailyRepo.findByAccountIdAndDayIn(accountId, dates)
                .associateBy { it.day }

        val entities = events.mapNotNull { (eventDate, event) ->
            dailyMap[eventDate]?.let { daily ->
                ScheduleContentEntity(
                        id = 0L,
                        dailySchedulesId = daily.id,
                        accountId = accountId,
                        externalEventId = event.externalEventId,
                        relationTypes = event.relationTypes,
                        title = event.title,
                        location = event.location,
                        contents = event.contents,
                        useYn = event.useYn,
                        tagIds = event.tagIds,
                        startTime = event.startTime,
                        endTime = event.endTime,
                        status = event.status,
                        createdAt = event.createdAt,
                        updatedAt = event.updatedAt
                )
            }
        }

        contentRepo.saveAll(entities)
        logger.info("Inserted ${entities.size} new events for $startDate~$endDate")

        updateCacheSafely(accountId, dates, dailyMap, entities)
    }



    private fun updateCacheSafely(
            accountId: Long,
            dates: List<LocalDate>,
            dailyMap: Map<LocalDate, DailyScheduleEntity>,
            entities: List<ScheduleContentEntity> // 저장한 이벤트 엔티티
    ) {
        val today = LocalDate.now()
        if (today in dates) {
            try {
                val todaySchedules = entities
                        .filter { dailyMap[today]?.id == it.dailySchedulesId }
                        .map { it.toDomain() }

                if (todaySchedules.isNotEmpty()) {
                    cachePort.put(accountId, today, todaySchedules, ttlSeconds = 3600)
                    logger.debug("Cache updated for accountId=$accountId, date=$today, size=${todaySchedules.size}")
                }
            } catch (ex: Exception) {
                logger.warn("Cache update failed for accountId=$accountId, date=$today", ex)
            }
        }
    }

    @Transactional
    override fun saveInternalEvent(accountId: Long, event: ScheduleContent) {
        val day = event.createdAt.toLocalDate()

        val daily = dailyRepo.findByAccountIdAndDay(accountId, day)
                ?: dailyRepo.save(
                        DailyScheduleEntity(
                                id = 0L, accountId = accountId, day = day,
                                createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
                        )
                )

        val entity = ScheduleContentEntity(
                id = if (event.id == 0L) 0L else event.id,
                dailySchedulesId = daily.id,
                accountId = accountId,
                externalEventId = event.externalEventId,
                relationTypes = null,
                title = event.title,
                location = event.location,
                contents = event.contents,
                useYn = event.useYn,
                tagIds = event.tagIds,
                startTime = event.startTime,
                endTime = event.endTime,
                status = event.status,
                createdAt = event.createdAt,
                updatedAt = LocalDateTime.now()
        )

        contentRepo.save(entity)

        if (day == LocalDate.now()) {
            cachePort.evict(accountId, day)
        }
    }

    @Transactional
    override fun createDailySchedulesForRange(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate
    ) {
        // 이미 존재하는 날짜 조회
        val existingDays = dailyRepo.findByAccountIdAndDayBetween(accountId, startDate, endDate)
                .map { it.day }
                .toSet()

        val newDailies = mutableListOf<DailyScheduleEntity>()
        var day = startDate
        while (!day.isAfter(endDate)) {
            if (day !in existingDays) {
                newDailies += DailyScheduleEntity(
                        id = 0L,
                        accountId = accountId,
                        day = day,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                )
            }
            day = day.plusDays(1)
        }

        if (newDailies.isNotEmpty()) {
            dailyRepo.saveAll(newDailies)
            logger.info("Created ${newDailies.size} DailySchedules for $accountId ($startDate ~ $endDate)")
        } else {
            logger.info("All DailySchedules already exist for $accountId ($startDate ~ $endDate)")
        }
    }

}