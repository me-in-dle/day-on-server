package com.day.on.calendar.jpa

import com.day.on.calendar.model.DailySchedule
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "daily_schedules")
class DailyScheduleEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false, columnDefinition = "BIGINT")
        val accountId: Long, // 유니크인덱스추가 ^^

        // string으로해돋상관무 타입지정하기 ^^
        @Column(name = "day", nullable = false)
        val day: LocalDate, // 유니크인덱스추가 ^^

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): DailySchedule {
        return DailySchedule(
                id = this.id,
                accountId = this.accountId,
                day = this.day,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: DailySchedule): DailyScheduleEntity {
            return DailyScheduleEntity(
                    id = domain.id,
                    accountId = domain.accountId,
                    day = domain.day,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}