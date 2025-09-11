package com.day.on.websocket.usecase.outbound

interface MessagePublishPort {
    fun publishBroadcast(payload: String)
    fun publishToServer(serverId: String, payload: String)
}