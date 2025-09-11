package com.day.on.websocket.usecase.outbound

interface MessageDispatchPort {
    fun dispatchBroadcast(payload: String)
    fun dispatchPersonal(userId: String, body: String)
}