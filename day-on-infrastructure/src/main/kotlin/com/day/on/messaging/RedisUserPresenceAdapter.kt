package com.day.on.messaging

import com.day.on.websocket.usecase.outbound.UserPresencePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisUserPresenceAdapter(
    private val redis: StringRedisTemplate
) : UserPresencePort {

    override fun bind(userId: String, serverId: String, sessionId: String) {
        redis.opsForSet().add("user:$userId:sessions", sessionId)
        redis.opsForValue().set("session:$sessionId:server", serverId)
        redis.opsForSet().add("server:$serverId:users", userId)
    }

    override fun unbind(userId: String, sessionId: String) {
        val serverId = redis.opsForValue().get("session:$sessionId:server") ?: return
        redis.opsForSet().remove("user:$userId:sessions", sessionId)
        redis.delete("session:$sessionId:server")

        // 세션이 더 없으면 user-server 매핑 제거
        val remaining = redis.opsForSet().size("user:$userId:sessions") ?: 0
        if (remaining == 0L) {
            redis.delete("user:$userId:sessions")
            redis.opsForSet().remove("server:$serverId:users", userId)
        }
    }

    override fun findServerIdBy(userId: String): String? {
        val sessions = redis.opsForSet().members("user:$userId:sessions") ?: return null
        val firstSession = sessions.firstOrNull() ?: return null
        return redis.opsForValue().get("session:$firstSession:server")
    }
}

