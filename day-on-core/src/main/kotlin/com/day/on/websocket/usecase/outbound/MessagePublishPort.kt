package com.day.on.websocket.usecase.outbound

interface MessagePublishPort {
    fun publishBroadcast(payload: String)
    fun publishToUser(userId: String, body: String)
}