package com.day.on.calendar.endpoint

import com.day.on.account.endpoint.const.HeaderName
import com.day.on.account.type.ConnectType
import com.day.on.api.response.SuccessResponse
import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarOAuthController(
        private val calendarOAuthUseCase: CalendarOAuthUseCase
) {
    /*
    * 최초 진입시 캘린더 권한 요청
    */
    @GetMapping("/oauth-url")
    fun getOAuthUrl(
            @RequestHeader(HeaderName.ACCOUNT_ID) accountId: Long
    ): SuccessResponse<String> {
        val url = calendarOAuthUseCase.generateCalendarOAuthUrl(accountId)
        return SuccessResponse.of(url)
    }

}