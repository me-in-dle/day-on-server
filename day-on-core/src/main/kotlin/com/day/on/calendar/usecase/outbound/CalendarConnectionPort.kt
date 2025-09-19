package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.model.CalendarConnection

interface CalendarConnectionPort {
    fun existsByAccountIdAndIsActive(accountId: Long): Boolean
    fun findByAccountId(accountId: Long): CalendarConnection?
    fun save(connection: CalendarConnection) : CalendarConnection

}