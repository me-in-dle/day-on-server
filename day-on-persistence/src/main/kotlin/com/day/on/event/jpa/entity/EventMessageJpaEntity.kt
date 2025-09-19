package com.day.on.event.jpa.entity

import com.day.on.event.model.EventMessage
import com.day.on.event.model.EventMessageStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event_message")
data class EventMessageJpaEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "user_id", nullable = false)
        val userId: Long,

        @Column(name = "meta_data", length = 255)
        val metaData: String? = null,


        @Column(name = "message", columnDefinition = "TEXT")
        val message: String? = null,

        @Column(name = "status", length = 20)
        @Enumerated(EnumType.STRING)
        val status: EventMessageStatus = EventMessageStatus.PUBLISHED,


        @Column(name = "published_at", nullable = false)
        val publishedAt: LocalDateTime = LocalDateTime.now(),


        @Column(name = "received_at")
        val receivedAt: LocalDateTime? = null
) {
        fun toDomain() = EventMessage(
                id = id ?: 0,
                userId = userId,
                metaData = metaData,
                message = message,
                status = status,
                publishedAt = publishedAt,
                receivedAt = receivedAt
        )

        companion object {
                fun fromDomain(message: EventMessage) = EventMessageJpaEntity(
                        userId = message.userId,
                        metaData = message.metaData,
                        message = message.message,
                        status = message.status,
                        publishedAt = message.publishedAt,
                        receivedAt = message.receivedAt
                )
        }
}