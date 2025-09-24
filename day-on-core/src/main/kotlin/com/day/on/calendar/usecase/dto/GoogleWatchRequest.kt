package com.day.on.calendar.usecase.dto

data class GoogleWatchRequest(
    val id: String,
    val type: String = "web_hook",
    val address: String,
    val params: Map<String, String>? = null,
)
