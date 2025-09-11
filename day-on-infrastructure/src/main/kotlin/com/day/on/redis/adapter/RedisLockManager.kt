package com.day.on.redis.adapter

import com.day.on.common.outbound.LockManager
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class RedisLockManager(
    private val redisCacheAdapter: RedisCacheAdapter,
) : LockManager {
    override fun <T> lock(key: String, block: () -> T): T {
        check(!TransactionSynchronizationManager.isSynchronizationActive()) { "Cannot acquire lock within a transaction" }

        val lock = RedisLock(key)

        if (lock.tryLock()) {
            throw IllegalStateException("Failed to acquire lock for key: $key")
        }

        return try {
            block()
        } finally {
            lock.unlock()
        }
    }

    inner class RedisLock(private val key: String) {
        private val lockKey = REDIS_LOCK_PREFIX + key
        private val lockTimeMillis = DEFAULT_LOCK_TIME_MILLIS

        fun tryLock(): Boolean {
            return redisCacheAdapter.putIfAbsent(lockKey, "LOCKED", lockTimeMillis) == true
        }

        fun unlock() {
            redisCacheAdapter.delete(lockKey)
        }
    }

    companion object {
        private const val DEFAULT_LOCK_TIME_MILLIS = 3000L
        private const val REDIS_LOCK_PREFIX = "REDIS_LOCK:"
    }
}