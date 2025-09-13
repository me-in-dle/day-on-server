package com.day.on.redis.adapter

import com.day.on.common.outbound.CachePort
import com.day.on.extension.parseJson
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import com.day.on.extension.toJson
import java.time.Duration.ofMillis

@Component
class RedisCacheAdapter(
    private val redisTemplate: RedisTemplate<String, Any>
) : CachePort {
    override fun <T : Any> put(key: String, value: T, ttlMillis: Long) {
        redisTemplate.opsForValue()[key, value.toJson()] = ofMillis(ttlMillis)
    }

    override fun <T : Any> putIfAbsent(key: String, value: T, ttlMillis: Long): Boolean? {
        return redisTemplate.opsForValue().setIfAbsent(key, value.toJson(), ofMillis(ttlMillis))
    }

    override fun <T : Any> get(key: String, type: Class<T>): T? {
        return redisTemplate.opsForValue()[key]?.toJson()?.parseJson(type)
    }

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }

}