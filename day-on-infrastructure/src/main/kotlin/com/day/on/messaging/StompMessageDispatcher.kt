package com.day.on.messaging

import com.day.on.websocket.usecase.outbound.MessageDispatchPort
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class StompMessageDispatcher(
    private val template: SimpMessagingTemplate
) : MessageDispatchPort {

    override fun dispatchBroadcast(payload: String) {
        template.convertAndSend("/topic/broadcast", payload)
    }

    // TODO : 개인화 하기 ^^
    override fun dispatchPersonal(userId: String, body: String) {
        template.convertAndSend( "/queue/$userId/notification", body)
    }
}