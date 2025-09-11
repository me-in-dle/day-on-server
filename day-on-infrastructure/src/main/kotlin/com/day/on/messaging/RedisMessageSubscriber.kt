package com.day.on.messaging

import com.day.on.websocket.usecase.inbound.MessageSubscribeUseCase
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class RedisMessageSubscriber(
    private val useCase : MessageSubscribeUseCase
) : MessageListener {

    private val om = jacksonObjectMapper()

    data class PersonalMessage(val targetUserId: String, val body: String)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val payload = String(message.body)
        val channel = pattern?.toString(Charsets.UTF_8) ?: return

        when {
            channel.startsWith("topic:") -> useCase.onBroadcastReceived(payload)
            channel.startsWith("server:") -> {
                val msg = om.readValue<PersonalMessage>(payload)
                useCase.onPersonalMessageReceived(msg.targetUserId, msg.body)
            }
        }
    }

}
