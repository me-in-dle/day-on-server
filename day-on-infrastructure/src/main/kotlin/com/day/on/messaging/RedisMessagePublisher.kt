package com.day.on.messaging

import com.day.on.websocket.usecase.outbound.MessagePublishPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisMessagePublisher(
    private val redis: StringRedisTemplate
) : MessagePublishPort {

    override fun publishBroadcast(payload: String) {
        redis.convertAndSend("topic:system.broadcast", payload)
    }

    override fun publishToUser(userId: String, body: String) {
        val json = """{"userId":"$userId","body":"$body"}"""
        redis.convertAndSend("topic:user-messages", json)
    }

}
