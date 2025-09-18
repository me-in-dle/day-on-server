package com.day.on.calendar.client

import com.day.on.calendar.adapter.GoogleCalendarOauthProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GoogleCalendarOauthProperties::class)
class CalendarConfig {
}