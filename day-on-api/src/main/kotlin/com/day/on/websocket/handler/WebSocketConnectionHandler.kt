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
        // TODO : 웹소켓 예외처리 ++ 게이트웨이랑 연결하는 걸로 수정
        val acc = StompHeaderAccessor.wrap(event.message)
        // 서버 세션 id 검증해야돼 -> jsession 있는지 검증하고 , 해당 서버가 맞다면 userId를 가져오고
        // val sessionId = StompHeaderAccessor.getSessionId()
        val userId = acc.sessionAttributes?.get("ACCOUNT_ID") as? String
            ?: throw IllegalArgumentException("ACCOUNT_ID missing in handshake")
       val sessionId = acc.sessionId ?: error("Session ID missing")

        acc.user = StompPrincipal(userId)

        // TODO : 로컬 테이블 등록
        useCase.handleConnection(userId, sessionId)
    }


    // TODO : 추후 구독 on/off 설정 시 구독 핸들러 추가

    @EventListener
    fun handleWebSocketDisconnect(event: SessionDisconnectEvent) {
        val acc = StompHeaderAccessor.wrap(event.message)
        val userId = (acc.user as? StompPrincipal)?.userId ?: return
        val sessionId = acc.sessionId ?: return

        // TODO : 로컬 테이블 해제
        useCase.handleDisconnection(userId, sessionId)
    }
}
