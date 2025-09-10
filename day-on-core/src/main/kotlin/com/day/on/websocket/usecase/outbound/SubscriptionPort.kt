package com.day.on.websocket.usecase.outbound

import com.day.on.websocket.model.UserSubscription

interface SubscriptionPort {
    fun addSubscription(subscription: UserSubscription)
    fun removeSubscription(subscription: UserSubscription)
    fun removeAllUserSubscriptions(userId: String)
    fun getTopicSubscribers(topic: String): Set<String>
}