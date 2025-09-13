package com.day.on.websocket.config

import com.day.on.websocket.principal.StompPrincipal
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
class WebSocketMessageBrokerCustomConfigurer : WebSocketMessageBrokerConfigurer {
    override fun configureClientInboundChannel(registry: ChannelRegistration) {
        registry.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
                val acc = StompHeaderAccessor.wrap(message)
                if (acc.command == StompCommand.CONNECT) {
                    val headersMap = acc.sessionAttributes?.get("headers") as? Map<*, *> ?: emptyMap<Any?, Any?>()
                    val accountId = headersMap.entries
                        .firstOrNull { it.key?.toString()?.equals("x-account-id", ignoreCase = true) == true }
                        ?.value
                        ?.toString()

                    require(!accountId.isNullOrBlank()) { "X-ACCOUNT-ID missing" }

                    acc.sessionAttributes!!["ACCOUNT_ID"] = accountId
                    acc.user = StompPrincipal(accountId)
                }
                return message
            }
        })
    }
}