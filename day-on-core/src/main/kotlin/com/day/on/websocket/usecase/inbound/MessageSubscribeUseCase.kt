package com.day.on.websocket.usecase.inbound

interface MessageSubscribeUseCase {
    fun onBroadcastReceived(payload: String)
    fun onPersonalMessageReceived(userId: String, body: String)
}