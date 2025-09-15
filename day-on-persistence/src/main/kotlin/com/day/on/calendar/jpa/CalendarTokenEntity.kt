package com.day.on.calendar.jpa

import com.day.on.calendar.model.CalendarTokens
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "calendar_tokens")
data class CalendarTokenEntity(

        // TODO : access token은 redis 에 저장 여부 고려..
        @Id
        @Column(name = "connection_id")
        val connectionId: Long,

        @Column(name = "access_token", length = 1000, nullable = false)
        val accessToken: String,

        @Column(name = "refresh_token", length = 1000)
        val refreshToken: String?,

) {
    fun toDomain(): CalendarTokens {
        return CalendarTokens(
                connectionId = this.connectionId,
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
        )
    }

    companion object {
        fun fromDomain(domain: CalendarTokens): CalendarTokenEntity {
            return CalendarTokenEntity(
                    connectionId = domain.connectionId,
                    accessToken = domain.accessToken,
                    refreshToken = domain.refreshToken,
            )
        }
    }
}