package com.day.on.messaging

import com.day.on.messaging.RedisMessageSubscriber.WebSocketTopics.SERVER
import com.day.on.messaging.RedisMessageSubscriber.WebSocketTopics.SYSTEM_BROADCAST
import com.day.on.messaging.RedisMessageSubscriber.WebSocketTopics.TOPIC
import com.day.on.messaging.RedisMessageSubscriber.WebSocketTopics.USER_NOTIFICATION
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber(
    private val template: SimpMessagingTemplate
) : MessageListener {

    data class PersonalMessage(
        val targetUserId: String,
        val body: String
    )

    private val objectMapper = jacksonObjectMapper()

    object WebSocketTopics {
        const val SYSTEM_BROADCAST = "/topic/system.broadcast"
        const val USER_NOTIFICATION = "/queue/notification"
        const val TOPIC = "topic:"
        const val SERVER = "server:"
    }
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val payload = String(message.body)
        val channel = pattern?.toString(Charsets.UTF_8) ?: return

        if (channel.startsWith(TOPIC)) template.convertAndSend(SYSTEM_BROADCAST, payload)
        else if (channel.startsWith(SERVER)) {
            // 개인 서버 채널로 구독 (메세지 안에 targerUserId 포함해야함)
            val msg = objectMapper.readValue(payload, PersonalMessage::class.java)
            template.convertAndSendToUser(msg.targetUserId, USER_NOTIFICATION,msg.body)

        }
        template.convertAndSend(channel, payload)
    }
}
