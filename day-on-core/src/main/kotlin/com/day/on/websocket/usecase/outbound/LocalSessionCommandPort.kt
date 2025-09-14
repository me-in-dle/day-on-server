package com.day.on.websocket.usecase.outbound

interface LocalSessionCommandPort {
    fun register(sessionId: String, userId: String)
    fun unregister(sessionId: String)
}