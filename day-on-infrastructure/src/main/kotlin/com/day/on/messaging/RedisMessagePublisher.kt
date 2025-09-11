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

    override fun publishToServer(serverId: String, payload: String) {
        redis.convertAndSend("server:$serverId", payload)
    }
}
