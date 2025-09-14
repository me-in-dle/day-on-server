package com.day.on.websocket.usecase.outbound

interface NotificationPort {
    fun sendToKakao(userId: Long, message: String)
}