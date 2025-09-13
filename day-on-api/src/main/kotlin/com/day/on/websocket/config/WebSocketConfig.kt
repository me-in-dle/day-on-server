package com.day.on.websocket.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker( "/topic", "/queue") // 구독 경로
        registry.setApplicationDestinationPrefixes("/app") // 메세지 경로
        registry.setUserDestinationPrefix("/user") // 개별 경로
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws", "/ws-connect")
            .setAllowedOriginPatterns("*")
            .addInterceptors(WebSocketHandshakeInterceptor())
    }

}