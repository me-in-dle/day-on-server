package com.day.on.calendar.endpoint

import com.day.on.account.endpoint.const.HeaderName
import com.day.on.api.response.SuccessResponse
import com.day.on.calendar.dto.CalendarResponse
import com.day.on.calendar.dto.ScheduleContentResponse
import com.day.on.calendar.usecase.inbound.CalendarQueryUseCase
import com.day.on.calendar.usecase.dto.CalendarQueryResult
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarController (
        private val calendarQueryUseCase: CalendarQueryUseCase
) {
    /*
    * 메인 진입 시 오늘 일정 요청
    */
    @GetMapping("/{date}")
    fun getCalendarByDate(@RequestHeader(HeaderName.ACCOUNT_ID) accountId: Long, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate) : SuccessResponse<CalendarResponse> {
        return when (val result = calendarQueryUseCase.getByDate(accountId, date)) {
            is CalendarQueryResult.NotConnected ->
                SuccessResponse.of(CalendarResponse(isConnected = false, schedules = emptyList()))

            is CalendarQueryResult.Connected ->
                SuccessResponse.of(
                        CalendarResponse(
                                isConnected = true,
                                schedules = result.schedules.map { ScheduleContentResponse.from(it) }
                        )
                )
        }
    }
}