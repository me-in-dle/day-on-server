package com.day.on.calendar.jpa

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_connections",
        indexes = [
            Index(name = "idx_calendar_connection_01", columnList = "account_id"),
        ])
class CalendarConnectionEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false)
        val accountId: Long,

        @Enumerated(EnumType.STRING)
        @Column(name = "provider", nullable = false)
        val provider: ConnectType,

        @Column(name = "is_active", nullable = false)
        val isActive: Boolean = true,

        @Column(name = "last_synced", nullable = false)
        val lastSynced: LocalDateTime,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): CalendarConnection {
        return CalendarConnection(
                id = this.id,
                accountId = this.accountId,
                provider = this.provider,
                isActive = this.isActive,
                lastSynced = this.lastSynced,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: CalendarConnection): CalendarConnectionEntity {
            return CalendarConnectionEntity(
                    id = domain.id,
                    accountId = domain.accountId,
                    provider = domain.provider,
                    isActive = domain.isActive,
                    lastSynced = domain.lastSynced,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}