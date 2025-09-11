package com.day.on.redis.adapter

import com.day.on.common.outbound.CachePort
import com.day.on.extension.parseJson
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration.ofSeconds
import com.day.on.extension.toJson

@Component
class RedisCacheAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : CachePort {
    override fun <T : Any> put(key: String, value: T, ttlSeconds: Long) {
        redisTemplate.opsForValue()[key, value.toJson()] = ofSeconds(ttlSeconds)
    }

    override fun <T : Any> putIfAbsent(key: String, value: T, ttlSeconds: Long): Boolean? {
        return redisTemplate.opsForValue().setIfAbsent(key, value.toJson(), ofSeconds(ttlSeconds))
    }

    override fun <T : Any> get(key: String, type: Class<T>): T? {
        return redisTemplate.opsForValue()[key]?.parseJson(type)
    }

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }

}