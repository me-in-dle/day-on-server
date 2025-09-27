package com.day.on.calendar.endpoint

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
class HealthController {

    @RequestMapping("/", method = [RequestMethod.GET, RequestMethod.POST])
    fun rootHealth(): String = "ok"
}