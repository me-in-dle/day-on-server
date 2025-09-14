package com.day.on.websocket.usecase.outbound

import com.day.on.event.model.EventMessage

interface LifePatternCommandPort {
    fun saveReminderLog(userId: Long, meta: String, message: String): EventMessage
}