package com.day.on.websocket.service

import com.day.on.websocket.usecase.inbound.MessageSubscribeUseCase
import com.day.on.websocket.usecase.outbound.LocalSessionQueryPort
import com.day.on.websocket.usecase.outbound.MessageDispatchPort
import com.day.on.websocket.usecase.outbound.UserPresencePort
import org.springframework.stereotype.Service

@Service
class MessageDispatchService(
    private val presencePort: UserPresencePort,
    private val dispatchPort: MessageDispatchPort,
    private val sessionQuery: LocalSessionQueryPort
) : MessageSubscribeUseCase {

    override fun onBroadcastReceived(payload: String) {
        dispatchPort.dispatchBroadcast(payload)
    }

    override fun onPersonalMessageReceived(userId:String, body: String) {
        val sessions = presencePort.findSessionsBy(userId)

        sessions.forEach { sessionId ->
            if (sessionQuery.has(sessionId)) {
                dispatchPort.dispatchPersonal(userId, body)
            }
        }

    }
}