package com.day.on.websocket.usecase.inbound

interface WebSocketUseCase {
    // 소켓 이벤트 발행 -> model 생성 및 삭제, 브로드캐스트 요청 -> messaging 발행
//    fun subscribeToTopic(userId: String, topic : String)
//    fun unsubscribeFromTopic(userId: String, topic: String)
    fun handleConnection(userId: String, sessionId : String)
    fun handleDisconnection(userId: String, sessionId : String)
    fun broadcast(payload : String) // 알림시점 호출
    fun sendToUser(userId: String, body: String)
}