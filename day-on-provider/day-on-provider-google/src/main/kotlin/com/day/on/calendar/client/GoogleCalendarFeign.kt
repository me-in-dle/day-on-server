package com.day.on.calendar.client

import com.day.on.calendar.dto.GoogleCalendarEventsResponse
import com.day.on.calendar.dto.GoogleCalendarListResponse
import com.day.on.calendar.usecase.dto.GoogleWatchRequest
import com.day.on.calendar.usecase.dto.GoogleWatchResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "google-calendar", url = "https://www.googleapis.com/calendar/v3")
interface GoogleCalendarFeign {
    // 증분 동기화 (syncToken 사용)
    @GetMapping("/calendars/{calendarId}/events")
    fun listEventsWithSyncToken(
        @PathVariable("calendarId") calendarId: String,
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("syncToken") syncToken: String,
        @RequestParam("pageToken", required = false) pageToken: String? = null,
        @RequestParam("maxResults") maxResults: Int = 2500,
        @RequestParam("singleEvents") singleEvents: Boolean = true,
        @RequestParam("showDeleted") showDeleted: Boolean = true,
    ): GoogleCalendarEventsResponse

    @GetMapping("/calendars/{calendarId}/events")
    fun listEvents(
        @PathVariable("calendarId") calendarId: String,
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("singleEvents") singleEvents: Boolean = true,
        @RequestParam("maxResults") maxResults: Int = 2500,
        @RequestParam("pageToken", required = false) pageToken: String? = null,
        @RequestParam("showDeleted") showDeleted: Boolean = true,
    ): GoogleCalendarEventsResponse

    // 사용자의 캘린더 목록 조회 (primary 찾기(어떤 캘린더 id), timezone 얻기용)
    @GetMapping("/users/me/calendarList")
    fun listCalendarList(
        @RequestHeader("Authorization") authorization: String,
    ): GoogleCalendarListResponse

    @PostMapping("/calendars/{calendarId}/events/watch")
    fun watchCalendar(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable("calendarId") calendarId: String,
        @RequestBody body: GoogleWatchRequest,
    ): GoogleWatchResponse
}
