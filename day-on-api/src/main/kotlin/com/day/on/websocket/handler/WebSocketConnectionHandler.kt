package com.day.on.websocket.handler

import com.day.on.websocket.principal.StompPrincipal
import com.day.on.websocket.usecase.inbound.WebSocketUseCase
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketConnectionHandler(
    private val useCase: WebSocketUseCase
) {
    @EventListener
    fun handleWebSocketConnect(event: SessionConnectedEvent) {
        // TODO : 웹소켓 예외처리
        val acc = StompHeaderAccessor.wrap(event.message)
        val userId = acc.sessionAttributes?.get("ACCOUNT_ID") as? String
            ?: throw IllegalArgumentException("ACCOUNT_ID missing in handshake")
        val sessionId = acc.sessionId ?: error("Session ID missing")

        acc.user = StompPrincipal(userId)

        useCase.handleConnection(userId, sessionId)
    }


    @EventListener
    fun handleWebSocketDisconnect(event: SessionDisconnectEvent) {
        val acc = StompHeaderAccessor.wrap(event.message)
        val userId = (acc.user as? StompPrincipal)?.userId ?: return
        val sessionId = acc.sessionId ?: return

        useCase.handleDisconnection(userId, sessionId)
    }
}
