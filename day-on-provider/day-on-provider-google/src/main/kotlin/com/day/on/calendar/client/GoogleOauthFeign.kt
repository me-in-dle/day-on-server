package com.day.on.calendar.client

import com.day.on.calendar.dto.GoogleTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "google-oauth", url = "https://oauth2.googleapis.com", configuration = [FeignFormConfig::class])
interface GoogleOauthFeign {
    @PostMapping(value = ["/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun exchangeTokenForm(@RequestBody form: MultiValueMap<String, String>): GoogleTokenResponse
}