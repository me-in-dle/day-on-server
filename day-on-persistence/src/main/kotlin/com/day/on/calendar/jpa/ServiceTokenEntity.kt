package com.day.on.calendar.jpa

import com.day.on.common.model.ServiceTokens
import com.day.on.common.model.ServiceType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_tokens")
data class ServiceTokenEntity(

        // TODO : access token은 redis 에 저장 여부 고려.. + 패키지 이동
        @Id
        @Column(name = "account_id")
        val accountId: Long,

        @Id
        @Enumerated(EnumType.STRING)
        @Column(name = "service_type" , nullable = false)
        val serviceType: ServiceType,

        @Column(name = "access_token", length = 1000, nullable = false)
        val accessToken: String,

        @Column(name = "refresh_token", length = 1000)
        val refreshToken: String?,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()

) {
    fun toDomain(): ServiceTokens {
        return ServiceTokens(
                accountId = this.accountId,
                serviceType = this.serviceType,
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: ServiceTokens): ServiceTokenEntity {
            return ServiceTokenEntity(
                    accountId = domain.accountId,
                    serviceType = domain.serviceType,
                    accessToken = domain.accessToken,
                    refreshToken = domain.refreshToken,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}