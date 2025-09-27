package com.day.on.calendar.adapter

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.repository.ScheduleContentJpaRepository
import com.day.on.calendar.repository.query.ScheduleContentJpaQueryDslRepository
import com.day.on.calendar.usecase.outbound.ScheduleContentQueryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ScheduleContentQueryAdapter(
    private val scheduleContentJpaRepository: ScheduleContentJpaRepository,
    private val scheduleContentJpaQueryDslRepository: ScheduleContentJpaQueryDslRepository,
) : ScheduleContentQueryPort {
    override fun findScheduleContentBy(accountId: Long, dailyScheduleId: Long): List<ScheduleContent> {
        scheduleContentJpaQueryDslRepository.findScheduleContentsBy(accountId, dailyScheduleId)
            .map { it.toDomain() }
            .let { return it }
    }

}