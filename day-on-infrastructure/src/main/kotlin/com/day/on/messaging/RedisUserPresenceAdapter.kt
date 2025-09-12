package com.day.on.messaging

import com.day.on.websocket.usecase.outbound.UserPresencePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisUserPresenceAdapter(
    private val redis: StringRedisTemplate
) : UserPresencePort {

    override fun bind(userId: String, sessionId: String) {
        redis.opsForSet().add("user:$userId:sessions", sessionId)
    }

    override fun unbind(userId: String, sessionId: String) {
        redis.opsForSet().remove("user:$userId:sessions", sessionId)
        if ((redis.opsForSet().size("user:$userId:sessions") ?: 0) == 0L) {
            redis.delete("user:$userId:sessions")
        }
    }

    override fun findSessionsBy(userId: String): Set<String>  {
        return redis.opsForSet().members("user:$userId:sessions") ?: emptySet()
    }
}

