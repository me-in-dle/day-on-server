package com.day.on.websocket.service

import com.day.on.websocket.usecase.inbound.MessageSubscribeUseCase
import com.day.on.websocket.usecase.outbound.MessageDispatchPort
import com.day.on.websocket.usecase.outbound.UserPresencePort
import org.springframework.stereotype.Service

@Service
class MessageDispatchService(
    private val presencePort: UserPresencePort,
    private val dispatchPort: MessageDispatchPort
) : MessageSubscribeUseCase {

    override fun onBroadcastReceived(payload: String) {
        dispatchPort.dispatchBroadcast(payload)
    }

    override fun onPersonalMessageReceived(userId: String, body: String) {
        val sessions = presencePort.findSessionsBy(userId)

        sessions.forEach { sessionId ->
            // TODO : 로컬 세션에도 있는지 확인 후 push
            dispatchPort.dispatchPersonal(userId, body)
        }
    }
}