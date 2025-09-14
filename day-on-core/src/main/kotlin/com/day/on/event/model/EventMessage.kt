package com.day.on.event.model

import java.time.LocalDateTime

data class EventMessage(
    val id: Long? = null,
    val userId: Long,
    val metaData: String?,
    val message: String?,
    val status: EventMessageStatus = EventMessageStatus.PUBLISHED,
    val publishedAt: LocalDateTime = LocalDateTime.now(),
    val receivedAt: LocalDateTime? = null
)
