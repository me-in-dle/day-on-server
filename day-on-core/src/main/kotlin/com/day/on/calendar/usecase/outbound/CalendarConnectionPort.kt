package com.day.on.calendar.usecase.outbound

interface CalendarConnectionPort {
    fun existsByAccountIdAndIsActive(accountId: Long): Boolean
}