package com.day.on.calendar.adapter

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.usecase.outbound.CalendarCachePort
import com.day.on.common.outbound.CachePort
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CalendarCacheAdapter(
        private val redisCacheAdapter: CachePort,
        private val objectMapper: ObjectMapper
) : CalendarCachePort {
    override fun get(accountId: Long, date: LocalDate): List<ScheduleContent>? {
        val key = "calendar:$accountId:$date"
        val raw: String = redisCacheAdapter.get(key, String::class.java) ?: return null

        return try {
            val json = if (raw.startsWith("\"") && raw.endsWith("\"")) {
                objectMapper.readValue(raw, String::class.java)
            } else {
                raw
            }
            objectMapper.readValue(json, object : TypeReference<List<ScheduleContent>>() {})
        } catch (ex: Exception) {
            null // 역직렬화 실패 시
        }
    }

    override fun put(accountId: Long, date: LocalDate, schedules: List<ScheduleContent>, ttlSeconds: Long) {
        val key = "calendar:$accountId:$date"

        try {
            // Jackson으로 직접 JSON 문자열 생성
            val json = objectMapper.writeValueAsString(schedules)
            redisCacheAdapter.put(key, json, ttlSeconds * 1000)
        } catch (ex: Exception) {
            throw RuntimeException("Failed to serialize schedules for cache", ex)
        }
    }


}
