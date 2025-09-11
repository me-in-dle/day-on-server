package com.day.on.websocket.service

import com.day.on.websocket.usecase.inbound.MessageSubscribeUseCase
import com.day.on.websocket.usecase.outbound.MessageDispatchPort
import org.springframework.stereotype.Service

@Service
class MessageDispatchService(
    private val dispatchPort: MessageDispatchPort
) : MessageSubscribeUseCase {

    override fun onBroadcastReceived(payload: String) {
        dispatchPort.dispatchBroadcast(payload)
    }

    override fun onPersonalMessageReceived(userId: String, body: String) {
        dispatchPort.dispatchPersonal(userId, body)
    }
}