package com.day.on.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * 로컬 메모리 캐시 설정 (각 서버의 JVM 메모리에 저장)
 * 행정구역 데이터용 (불변 데이터)
 */
@Configuration
@EnableCaching
class CacheConfig {

    /**
     * 로컬 메모리 CacheManager
     */
    @Bean
    @Primary
    fun localCacheManager(): CacheManager {
        return ConcurrentMapCacheManager("allDistricts")
    }

}