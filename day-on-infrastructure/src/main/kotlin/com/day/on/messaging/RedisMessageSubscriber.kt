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

    data class PersonalMessage(val userId: String, val body: String)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val payload = String(message.body)
        val channel = message.channel.toString(Charsets.UTF_8)

        when (channel) {
            "topic:system.broadcast" -> useCase.onBroadcastReceived(payload)
            "topic:user-messages" -> {
                val msg = om.readValue<PersonalMessage>(payload)
                useCase.onPersonalMessageReceived(msg.userId, msg.body)
            }
        }
    }

}
