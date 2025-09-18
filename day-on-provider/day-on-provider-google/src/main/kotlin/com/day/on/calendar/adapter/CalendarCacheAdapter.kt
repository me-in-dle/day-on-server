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
        // RedisAdapter에서 String을 반환하도록 요청
        val raw: String = redisCacheAdapter.get(key, String::class.java) ?: return null

        val json = if (raw.startsWith("\"") && raw.endsWith("\"")) {
            // raw가 JSON으로 이스케이프된 문자열이면, objectMapper로 언에스케이프해서 실제 JSON 획득
            objectMapper.readValue(raw, String::class.java)
        } else {
            raw
        }

        return objectMapper.readValue(json, object : TypeReference<List<ScheduleContent>>() {})
    }

    override fun put(accountId: Long, date: LocalDate, schedules: List<ScheduleContent>, ttlSeconds: Long) {
        val key = "calendar:$accountId:$date"
        // 따라서 객체를 그대로 넘겨서(직렬화는 RedisAdapter가 하게) 한 번만 직렬화되도록 한다.
        redisCacheAdapter.put(key, schedules, ttlSeconds * 1000)
    }


}
