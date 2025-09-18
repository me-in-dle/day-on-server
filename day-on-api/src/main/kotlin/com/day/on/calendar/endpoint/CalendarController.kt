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
        val result = calendarQueryUseCase.getByDate(accountId, date)

        val (isConnected, connectType, schedules) = when (result) {
            is CalendarQueryResult.Connected -> Triple(true, result.connectType?.name, result.schedules)
            is CalendarQueryResult.NotConnected -> Triple(false, null, result.schedules)
        }

        val response = CalendarResponse(
                isConnected = isConnected,
                connectType = connectType,
                schedules = schedules.map { ScheduleContentResponse.from(it) }
        )

        return SuccessResponse.of(response)
    }


}