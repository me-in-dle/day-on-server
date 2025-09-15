package com.day.on.calendar.jpa

import com.day.on.calendar.model.DailySchedule
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime


@Entity
@Table(name = "daily_schedules")
data class DailyScheduleEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false, length = 50)
        val accountId: Long,

        @Column(name = "day")
        val day: LocalDate?,

        @Column(name = "created_at")
        val createdAt: LocalDateTime?,

        @Column(name = "updated_at")
        val updatedAt: LocalDateTime?
) {
    fun toDomain(): DailySchedule {
        return DailySchedule(
                id = this.id,
                accountId = this.accountId,
                day = this.day ?: LocalDate.now(),
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