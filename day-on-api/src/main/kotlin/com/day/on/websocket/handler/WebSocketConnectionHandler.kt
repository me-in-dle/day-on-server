package com.day.on.websocket.handler

import com.day.on.session.LocalSessionManager
import com.day.on.websocket.principal.StompPrincipal
import com.day.on.websocket.usecase.inbound.WebSocketUseCase
import org.springframework.context.event.EventListener
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketConnectionHandler(
    private val useCase: WebSocketUseCase,
    private val localSessionManager: LocalSessionManager
) {
    @EventListener
    fun handleWebSocketConnect(event: SessionConnectedEvent) {
        // TODO : 웹소켓 예외처리

        val acc = StompHeaderAccessor.wrap(event.message)

//        val accountId = (acc.user as? StompPrincipal)?.accountId
//            ?: throw IllegalArgumentException("No Principal set on connect")

        val connectMsg = acc.messageHeaders["simpConnectMessage"] as? Message<*>
            ?: throw IllegalArgumentException("No CONNECT message in headers")

        val connectAcc = StompHeaderAccessor.wrap(connectMsg)

        val accountId = connectAcc.sessionAttributes?.get("ACCOUNT_ID") as? String
            ?: throw IllegalArgumentException("ACCOUNT_ID missing in handshake")
        val sessionId = acc.sessionId ?: error("Session ID missing")

        localSessionManager.register(sessionId, accountId);
        useCase.handleConnection(accountId, sessionId)
    }


    // TODO : 추후 구독 on/off 설정 시 구독 핸들러 추가

    @EventListener
    fun handleWebSocketDisconnect(event: SessionDisconnectEvent) {
        val acc = StompHeaderAccessor.wrap(event.message)
        val userId = (acc.user as? StompPrincipal)?.accountId ?: return
        val sessionId = acc.sessionId ?: return

        // TODO : 로컬 테이블 해제
        localSessionManager.unregister(sessionId)
        useCase.handleDisconnection(userId, sessionId)
    }
}
