package com.day.on.messaging

import com.day.on.websocket.model.UserSubscription
import com.day.on.websocket.usecase.outbound.SubscriptionPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisSubscriptionAdapter(
    private val redis: StringRedisTemplate
) : SubscriptionPort {

    companion object {
        const val TOPIC_PREFIX = "topic:"
        const val USER_PREFIX = "user:"
    }

    override fun addSubscription(subscription: UserSubscription) {
        redis.opsForSet().add("$TOPIC_PREFIX${subscription.topic}", subscription.userId)
        redis.opsForSet().add("$USER_PREFIX${subscription.userId}:topics", subscription.topic)
    }

    override fun removeSubscription(subscription: UserSubscription) {
        redis.opsForSet().remove("$TOPIC_PREFIX${subscription.topic}", subscription.userId)
        redis.opsForSet().remove("$USER_PREFIX${subscription.userId}:topics", subscription.topic)
    }

    override fun removeAllUserSubscriptions(userId: String) {
        val userKey = "$USER_PREFIX$userId:topics"
        val topics = redis.opsForSet().members(userKey) ?: return
        topics.forEach { topic ->
            redis.opsForSet().remove("$TOPIC_PREFIX$topic", userId)
        }
        redis.delete(userKey)
    }

    override fun getTopicSubscribers(topic: String): Set<String> =
        redis.opsForSet().members("$TOPIC_PREFIX$topic")?.toSet() ?: emptySet()
}
