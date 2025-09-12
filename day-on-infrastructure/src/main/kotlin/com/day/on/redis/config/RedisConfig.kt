package com.day.on.redis.config

import com.day.on.messaging.RedisMessageSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 0

    @Bean
    fun applyRedisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun applyRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.setEnableTransactionSupport(true)

        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = StringRedisSerializer()

        return redisTemplate
    }

    /*
    * Redis pub + sub 메세지 처리 Listener
    */
    @Bean
    fun redisMessageListener(
        connectionFactory: RedisConnectionFactory,
        subscriber: RedisMessageSubscriber
    ) : RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)

        container.addMessageListener(subscriber, ChannelTopic("topic:system.broadcast"))
        container.addMessageListener(subscriber, PatternTopic("server:*"))

        return container

    }
}