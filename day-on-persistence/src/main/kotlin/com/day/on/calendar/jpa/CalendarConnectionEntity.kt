package com.day.on.calendar.jpa

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_connections")
data class CalendarConnectionEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false)
        val accountId: Long,

        @Enumerated(EnumType.STRING)
        @Column(name = "provider", nullable = false)
        val provider: ConnectType,

        @Column(name = "account_email", nullable = false)
        val accountEmail: String,

        @Column(name = "account_name")
        val accountName: String?,

        @Column(name = "is_active", nullable = false)
        val isActive: Boolean = true,

        @Column(name = "sync_enabled", nullable = false)
        val syncEnabled: Boolean = true,

        @Column(name = "last_synced")
        val lastSynced: LocalDateTime?,

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
                accountEmail = this.accountEmail,
                accountName = this.accountName,
                isActive = this.isActive,
                syncEnabled = this.syncEnabled,
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
                    accountEmail = domain.accountEmail,
                    accountName = domain.accountName,
                    isActive = domain.isActive,
                    syncEnabled = domain.syncEnabled,
                    lastSynced = domain.lastSynced,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}