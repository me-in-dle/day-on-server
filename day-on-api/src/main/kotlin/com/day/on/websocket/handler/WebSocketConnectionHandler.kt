package com.day.on.websocket.handler

import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import com.day.on.websocket.usecase.inbound.WebSocketUseCase
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent

@Component
class WebSocketConnectionHandler (private val webSocketUseCase: WebSocketUseCase){

    @EventListener
    fun handleWebSocketConnect(event : SessionConnectedEvent) {
        val stompHeaderAccessor = StompHeaderAccessor.wrap(event.message)

        // API Gateway에서 보낸 ACCOUNT_ID 헤더 읽기
        // TODO : 웹소켓 예외 처리
        val userId = stompHeaderAccessor.getNativeHeader("ACCOUNT_ID")?.get(0)
            ?: throw IllegalArgumentException("ACCOUNT_ID header missing")

        val sessionId = stompHeaderAccessor.sessionId
            ?: throw IllegalArgumentException("Session ID missing")

        // 세션 principal 객체에 저장 -> security에서 관리
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val principal = UsernamePasswordAuthenticationToken(userId, null, authorities)
        stompHeaderAccessor.user = principal

        println("User connected: userId=$userId, sessionId=$sessionId")
    }

    @EventListener
    fun handleSubscription(event: SessionSubscribeEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val userId = accessor.user?.name ?: return
        val topic = accessor.destination ?: return
        webSocketUseCase.subscribeToTopic(userId, topic)
    }

    @EventListener
    fun handleUnsubscription(event: SessionUnsubscribeEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val userId = accessor.user?.name ?: return
        val topic = accessor.destination ?: return
        webSocketUseCase.unsubscribeFromTopic(userId, topic)
    }

    @EventListener
    fun handleWebSocketDisconnect(event: SessionDisconnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val userId = accessor.user?.name ?: return
        webSocketUseCase.handleDisconnection(userId)
    }

}