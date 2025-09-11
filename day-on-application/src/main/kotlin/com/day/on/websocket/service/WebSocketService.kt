package com.day.on.websocket.service

import com.day.on.websocket.usecase.inbound.WebSocketUseCase
import com.day.on.websocket.usecase.outbound.MessagePublishPort
import com.day.on.websocket.usecase.outbound.UserPresencePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WebSocketService (
    private val presencePort: UserPresencePort,
    private val messagePublishPort : MessagePublishPort,
    @Value("\${app.server-id}") private val serverId: String
) : WebSocketUseCase {

    override fun handleConnection(userId: String, sessionId: String) {
        // 유저 - 서버 Redis에 매핑
        presencePort.bind(userId, serverId, sessionId)
    }

    override fun handleDisconnection(userId: String, sessionId : String) {
        presencePort.unbind(userId, sessionId)
    }

    override fun broadcast(payload: String) {
        messagePublishPort.publishBroadcast(payload)
    }

    override fun sendToUser(userId: String, body: String) {
        val targetServerId = presencePort.findServerIdBy(userId) ?: return
        val json = """{"targetUserId":"$userId","body":"$body"}"""
        messagePublishPort.publishToServer(targetServerId, json)
    }
}
