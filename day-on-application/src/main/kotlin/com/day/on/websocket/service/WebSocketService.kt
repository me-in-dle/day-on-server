package com.day.on.websocket.service

import com.day.on.websocket.model.UserSubscription
import com.day.on.websocket.usecase.inbound.WebSocketUseCase
import com.day.on.websocket.usecase.outbound.MessagePublishPort
import com.day.on.websocket.usecase.outbound.SubscriptionPort
import org.springframework.stereotype.Service

@Service
class WebSocketService (
    private val subscriptionPort: SubscriptionPort,
    private val messagePublishPort : MessagePublishPort
) : WebSocketUseCase {

    override fun subscribeToTopic(userId: String, topic: String) {
        subscriptionPort.addSubscription(UserSubscription(userId, topic))
    }

    override fun unsubscribeFromTopic(userId: String, topic: String) {
        subscriptionPort.removeSubscription(UserSubscription(userId, topic))
    }

    override fun handleDisconnection(userId: String) {
        subscriptionPort.removeAllUserSubscriptions(userId)
    }

    override fun broadcast(payload: String, topic: String) {
        messagePublishPort.publishBroadcast(payload)
    }


}