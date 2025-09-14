package com.day.on.websocket.usecase.outbound

interface LocalSessionQueryPort {
    fun has(sessionId: String): Boolean
}