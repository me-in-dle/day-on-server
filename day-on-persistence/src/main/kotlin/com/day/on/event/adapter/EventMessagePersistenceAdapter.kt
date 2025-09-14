package com.day.on.event.adapter

import com.day.on.event.jpa.entity.EventMessageJpaEntity
import com.day.on.event.jpa.repository.EventMessageRepository
import com.day.on.event.model.EventMessage
import com.day.on.websocket.usecase.outbound.LifePatternCommandPort
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventMessagePersistenceAdapter(
        private val repository: EventMessageRepository
) : LifePatternCommandPort {

    override fun saveReminderLog(userId: Long, meta: String, message: String): EventMessage {
        val domain = EventMessage(
                userId = userId,
                metaData = meta,
                message = message,
        )
        val saved = repository.save(EventMessageJpaEntity.fromDomain(domain))
        return saved.toDomain()
    }
}