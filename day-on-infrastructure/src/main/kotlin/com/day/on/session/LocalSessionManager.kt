package com.day.on.session

import com.day.on.websocket.usecase.outbound.LocalSessionCommandPort
import com.day.on.websocket.usecase.outbound.LocalSessionQueryPort
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class LocalSessionManager : LocalSessionQueryPort, LocalSessionCommandPort {
    private val sessionMap = ConcurrentHashMap<String, String>()

    override fun has(sessionId: String): Boolean = sessionMap.containsKey(sessionId)
    override fun register(sessionId: String, userId: String) {
        sessionMap[sessionId] = userId
    }

    override fun unregister(sessionId: String) {
        sessionMap.remove(sessionId)
    }
}