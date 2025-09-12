package com.day.on.websocket.usecase.outbound

interface UserPresencePort {
    fun bind(userId: String, sessionId : String)
    fun unbind(userId: String, sessionId : String)
    fun findSessionsBy(userId: String): Set<String>
}