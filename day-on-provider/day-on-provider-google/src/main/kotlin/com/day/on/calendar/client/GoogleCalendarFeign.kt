package com.day.on.calendar.client

import com.day.on.calendar.dto.GoogleCalendarEventsResponse
import com.day.on.calendar.dto.GoogleCalendarListResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "google-calendar", url = "https://www.googleapis.com/calendar/v3")
interface GoogleCalendarFeign {
    @GetMapping("/calendars/{calendarId}/events")
    fun listEvents(
            @PathVariable("calendarId") calendarId: String,
            @RequestHeader("Authorization") authorization: String,
            @RequestParam("timeMin") timeMin: String,    // RFC3339
            @RequestParam("timeMax") timeMax: String,    // RFC3339
            @RequestParam("singleEvents") singleEvents: Boolean = true,
            @RequestParam("maxResults") maxResults: Int = 2500,
            @RequestParam("pageToken", required = false) pageToken: String? = null,
            @RequestParam("showDeleted") showDeleted: Boolean = false
    ): GoogleCalendarEventsResponse

    // 사용자의 캘린더 목록 조회 (primary 찾기(어떤 캘린더 id), timezone 얻기용)
    @GetMapping("/users/me/calendarList")
    fun listCalendarList(
            @RequestHeader("Authorization") authorization: String
    ): GoogleCalendarListResponse
}