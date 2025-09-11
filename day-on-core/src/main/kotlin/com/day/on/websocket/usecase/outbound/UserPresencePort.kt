package com.day.on.websocket.usecase.outbound

interface UserPresencePort {
    fun bind(userId: String, serverId: String, sessionId : String)
    fun unbind(userId: String, sessionId : String)
    fun findServerIdBy(userId: String): String?
}