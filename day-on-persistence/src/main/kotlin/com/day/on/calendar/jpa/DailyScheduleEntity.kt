package com.day.on.calendar.jpa

import com.day.on.calendar.model.DailySchedule
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "daily_schedules")
data class DailyScheduleEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false, columnDefinition = "BIGINT")
        val accountId: Long,

        @Column(name = "day", nullable = false)
        val day: LocalDateTime = LocalDateTime.now(),

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