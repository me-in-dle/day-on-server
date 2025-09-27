package com.day.on.calendar.repository.query

import com.day.on.calendar.jpa.QDailyScheduleEntity
import com.day.on.calendar.jpa.QScheduleContentEntity
import com.day.on.calendar.jpa.ScheduleContentEntity
import com.day.on.calendar.model.TaskStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface ScheduleContentJpaQueryDslRepository {
    fun findScheduleContentsBy(accountId: Long, dailyId: Long): List<ScheduleContentEntity>
}

@Repository
@Transactional(readOnly = true)
class ScheduleContentJpaQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ScheduleContentJpaQueryDslRepository {

    private val qDailyScheduleEntity = QDailyScheduleEntity.dailyScheduleEntity
    private val qScheduleContentEntity = QScheduleContentEntity.scheduleContentEntity
    override fun findScheduleContentsBy(accountId: Long, dailyId: Long): List<ScheduleContentEntity> {
        return jpaQueryFactory
            .select(qScheduleContentEntity)
            .from(qDailyScheduleEntity)
            .join(qScheduleContentEntity).on(qScheduleContentEntity.dailySchedulesId.eq(qDailyScheduleEntity.id))
            .where(
                qDailyScheduleEntity.accountId.eq(accountId),
                qDailyScheduleEntity.id.eq(dailyId),
                qScheduleContentEntity.useYn.eq("Y"),
                qScheduleContentEntity.status.eq(TaskStatus.COMPLETED)
            )
            .orderBy(qScheduleContentEntity.startTime.asc())
            .fetch()
    }
}