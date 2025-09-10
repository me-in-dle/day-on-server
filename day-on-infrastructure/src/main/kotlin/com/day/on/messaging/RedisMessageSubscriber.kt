package com.day.on.messaging

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber(
    private val template: SimpMessagingTemplate
) : MessageListener {
    // 서버끼리 동기화
    object WebSocketTopics {
        const val SYSTEM_BROADCAST = "/topic/system.broadcast"
        const val USER_NOTIFICATION = "/topic/user.notification"
    }
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val payload = String(message.body)
        val channel = pattern?.toString(Charsets.UTF_8) ?: WebSocketTopics.SYSTEM_BROADCAST
        template.convertAndSend(channel, payload)
    }
}
