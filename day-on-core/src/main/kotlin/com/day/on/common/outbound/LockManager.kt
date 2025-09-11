package com.day.on.common.outbound

interface LockManager {
    fun <T> lock(
        key: String,
        block: () -> T,
    ): T
}