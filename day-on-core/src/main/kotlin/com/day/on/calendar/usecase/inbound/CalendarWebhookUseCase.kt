package com.day.on.calendar.usecase.inbound

interface CalendarWebhookUseCase {
    fun handleWebhookNotification(channelId: String, resourceId: String)
}
