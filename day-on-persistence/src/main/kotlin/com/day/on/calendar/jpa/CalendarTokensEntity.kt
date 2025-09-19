package com.day.on.calendar.jpa

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarTokens
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_tokens")
class CalendarTokensEntity(

        // TODO : access token은 redis 에 저장 여부 고려..
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "account_id", nullable = false)
        val accountId: Long,

        @Enumerated(EnumType.STRING)
        @Column(name = "connect_type" , nullable = false)
        val connectType: ConnectType,

        // 찾아보기
        @Column(name = "access_token", length = 2000)
        val accessToken: String,

        @Column(name = "refresh_token", length = 2000, nullable = false)
        val refreshToken: String,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()

) {
    fun toDomain(): CalendarTokens {
        return CalendarTokens(
                accountId = this.accountId,
                connectType = this.connectType,
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: CalendarTokens): CalendarTokensEntity {
            return CalendarTokensEntity(
                    accountId = domain.accountId,
                    connectType = domain.connectType,
                    accessToken = domain.accessToken,
                    refreshToken = domain.refreshToken,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}