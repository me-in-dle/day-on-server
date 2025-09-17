package com.day.on.calendar.adapter

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.usecase.outbound.CalendarCachePort
import com.day.on.common.outbound.CachePort
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class CalendarCacheAdapter(
        private val redisCacheAdapter: CachePort,
        private val objectMapper: ObjectMapper
) : CalendarCachePort {
    override fun get(accountId: Long, date: LocalDate): List<ScheduleContent>? {
        val key = "calendar:$accountId:$date"
        val json = redisCacheAdapter.get(key, String::class.java) ?: return null
        return objectMapper.readValue(json, object : TypeReference<List<ScheduleContent>>() {})
    }

    override fun put(accountId: Long, date: LocalDate, schedules: List<ScheduleContent>, ttlSeconds: Long) {
        val key = "calendar:$accountId:$date"
        val json = objectMapper.writeValueAsString(schedules)
        redisCacheAdapter.put(key, json, ttlSeconds * 1000)
    }

}
