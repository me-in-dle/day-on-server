package com.day.on.calendar.repository

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.ScheduleContentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface ScheduleContentJpaRepository : JpaRepository<ScheduleContentEntity, Long> {
    fun findByDailySchedulesId(dailySchedulesId: Long): List<ScheduleContentEntity>
    @Modifying
    @Query("DELETE FROM ScheduleContentEntity e WHERE e.dailySchedulesId = :dailyId AND e.relationTypes = :relationType")
    fun deleteByDailySchedulesIdAndRelationTypes(
            @Param("dailyId") dailyId: Long,
            @Param("relationType") relationType: ConnectType
    )
}