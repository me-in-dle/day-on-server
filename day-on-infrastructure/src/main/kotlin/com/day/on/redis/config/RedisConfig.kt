package com.day.on.redis.config

import com.day.on.messaging.RedisMessageSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

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
    fun redisTemplate(): RedisTemplate<*, *> {
        return RedisTemplate<Any, Any>().apply {
            this.connectionFactory = applyRedisConnectionFactory()
            this.keySerializer = StringRedisSerializer()
            this.hashKeySerializer = StringRedisSerializer()
            this.valueSerializer = StringRedisSerializer()
        }
    }

    @Bean
    fun cacheManager(): CacheManager {
        val defaultCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()),
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()),
                )

        val cacheConfigurations =
            mapOf(
                "member" to defaultCacheConfiguration.entryTtl(Duration.ofMillis(1000 * 60 * 5)),
            )

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(applyRedisConnectionFactory())
            .withInitialCacheConfigurations(cacheConfigurations)
            .cacheDefaults(defaultCacheConfiguration)
            .build()
    }

    /*
    * Redis pub + sub 메세지 처리 Listener
    */
    @Bean
    fun redisMessageListener(
        connectionFactory: RedisConnectionFactory,
        subscriber: RedisMessageSubscriber
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)

        container.addMessageListener(subscriber, ChannelTopic("topic:system.broadcast"))
        container.addMessageListener(subscriber, ChannelTopic("topic:user-messages"))

        return container

    }
}