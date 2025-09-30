package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.DailyScheduleEntity
import com.day.on.calendar.jpa.ScheduleContentEntity
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import com.day.on.calendar.repository.DailyScheduleJpaRepository
import com.day.on.calendar.repository.ScheduleContentJpaRepository
import com.day.on.calendar.usecase.dto.ProviderEventChange
import com.day.on.calendar.usecase.outbound.CalendarCachePort
import com.day.on.calendar.usecase.outbound.CalendarEventSyncPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class CalendarEventSyncAdapter(
    private val dailyRepo: DailyScheduleJpaRepository,
    private val contentRepo: ScheduleContentJpaRepository,
    private val cachePort: CalendarCachePort,
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
            events: List<Pair<LocalDate, ScheduleContent>>,
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

        // 1. 미리 externalEventId 목록 추출
        val externalIds = events.mapNotNull { it.second.externalEventId }
        val existingEntities = contentRepo
                .findByAccountIdAndExternalEventIdIn(accountId, externalIds)
                .associateBy { it.externalEventId }

        val toSave = mutableListOf<ScheduleContentEntity>()

        // 2. 신규 or 업데이트 처리
        events.forEach { (eventDate, event) ->
            val daily = dailyMap[eventDate] ?: return@forEach

            val existing = event.externalEventId?.let { existingEntities[it] }
            if (existing != null) {
                // 이미 있는 이벤트면 업데이트
                existing.updateFrom(event)
                toSave += existing
            } else {
                // 없으면 신규 생성
                toSave += ScheduleContentEntity(
                        id = 0L,
                        dailySchedulesId = daily.id,
                        accountId = accountId,
                        externalEventId = event.externalEventId!!,
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
                        updatedAt = event.updatedAt,
                )
            }
        }

        // 3. 한번에 저장 (update + insert 모두 처리됨)
        if (toSave.isNotEmpty()) {
            contentRepo.saveAll(toSave)
            logger.info("Upserted ${toSave.size} events for $startDate~$endDate")
        }

        val today = LocalDate.now()
        if (today in dates) {
            updateCacheSafelyAfterCommit(accountId, today, toSave, emptyList())
        }
    }


    private fun updateCacheSafelyAfterCommit(
        accountId: Long,
        today: LocalDate,
        toSave: List<ScheduleContentEntity>,
        toDelete: List<String>
    ) {
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    try {
                        // 현재 캐시 읽기 (없으면 빈 리스트)
                        val current = cachePort.get(accountId, today)?.toMutableList() ?: mutableListOf()

                        // 삭제 반영
                        if (toDelete.isNotEmpty()) {
                            current.removeIf { it.externalEventId in toDelete }
                        }

                        // 저장/업데이트 반영
                        if (toSave.isNotEmpty()) {
                            val saveIds = toSave.mapNotNull { it.externalEventId }
                            current.removeIf { it.externalEventId in saveIds }
                            current.addAll(toSave.filter { it.createdAt.toLocalDate() == today }.map { it.toDomain() })
                        }

                        cachePort.put(accountId, today, current, ttlSeconds = 3600)
                        logger.debug("Cache updated after commit for accountId=$accountId, date=$today")
                    } catch (ex: Exception) {
                        logger.warn("Cache update failed for accountId=$accountId, date=$today", ex)
                    }
                }
            }
        )
    }


    @Transactional
    override fun saveInternalEvent(
        accountId: Long,
        event: ScheduleContent,
    ) {
        val day = event.createdAt.toLocalDate()

        val daily =
            dailyRepo.findByAccountIdAndDay(accountId, day)
                ?: dailyRepo.save(
                    DailyScheduleEntity(
                        id = 0L, accountId = accountId, day = day,
                        createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now(),
                    ),
                )

        val entity =
            ScheduleContentEntity(
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
                updatedAt = LocalDateTime.now(),
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
        endDate: LocalDate,
    ) {
        // 이미 존재하는 날짜 조회
        val existingDays =
            dailyRepo.findByAccountIdAndDayBetween(accountId, startDate, endDate)
                .map { it.day }
                .toSet()

        val newDailies = mutableListOf<DailyScheduleEntity>()
        var day = startDate
        while (!day.isAfter(endDate)) {
            if (day !in existingDays) {
                newDailies +=
                    DailyScheduleEntity(
                        id = 0L,
                        accountId = accountId,
                        day = day,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
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

    @Transactional
    override fun applyIncrementalChanges(
            accountId: Long,
            changes: List<ProviderEventChange>,
    ) {
        if (changes.isEmpty()) return

        // 1. 현재 존재하는 externalEventId들을 한방 조회
        val externalIds = changes.map { it.externalEventId }
        val existingEntities = contentRepo.findByAccountIdAndExternalEventIdIn(accountId, externalIds)
                .associateBy { it.externalEventId }

        val toSave = mutableListOf<ScheduleContentEntity>()
        val toDelete = mutableListOf<String>()
        val today = LocalDate.now()
        val todayChanged = mutableListOf<ScheduleContent>() // 오늘 변경만 추출 후 캐시 업뎃

        changes.forEach { change ->
            val daily = dailyRepo.findByAccountIdAndDay(accountId, change.eventDate)
            if (daily == null) {
                logger.info("Skip event ${change.externalEventId}: no DailySchedule for ${change.eventDate}")
                return@forEach
            }

            if (change.status == "cancelled") {
                toDelete += change.externalEventId
            } else {
                val existing = existingEntities[change.externalEventId]
                val newEntity = if (existing != null) {
                    change.schedule?.let { existing.updateFrom(it) }
                    existing
                } else {
                    ScheduleContentEntity(
                            id = 0L,
                            dailySchedulesId = daily.id,
                            accountId = accountId,
                            externalEventId = change.externalEventId,
                            relationTypes = ConnectType.GOOGLE,
                            title = change.schedule?.title ?: "(제목 없음)",
                            location = change.schedule?.location,
                            contents = change.schedule?.contents,
                            useYn = change.schedule?.useYn ?: "Y",
                            tagIds = change.schedule?.tagIds,
                            startTime = change.schedule?.startTime ?: LocalTime.MIDNIGHT,
                            endTime = change.schedule?.endTime ?: LocalTime.MIDNIGHT,
                            status = change.schedule?.status ?: TaskStatus.PENDING,
                            createdAt = change.schedule?.createdAt ?: LocalDateTime.now(),
                            updatedAt = LocalDateTime.now(),
                    )
                }
                toSave += newEntity
            }
        }

        // 2. 삭제/저장 bulk 처리
        if (toDelete.isNotEmpty()) {
            contentRepo.deleteByAccountIdAndExternalEventIdIn(accountId, toDelete)
        }
        if (toSave.isNotEmpty()) {
            contentRepo.saveAll(toSave)
        }

        // 3. 오늘 날짜 캐시 갱신
        if (changes.any { it.eventDate == today }) {
            updateCacheSafelyAfterCommit(accountId, today, toSave, toDelete)
        }

    }



    @Transactional
    override fun deleteByExternalEventId(
        accountId: Long,
        externalEventId: String,
    ) {
        contentRepo.deleteByAccountIdAndExternalEventId(accountId, externalEventId)
    }
}
