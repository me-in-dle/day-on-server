package com.day.on.calendar.usecase.outbound

import com.day.on.account.type.ConnectType
import java.time.LocalDate

interface PrefetchCalendarSyncPort {
    fun prefetchIfNeeded(
        accountId: Long,
        connectType: ConnectType,
        accessToken: String,
        currentDate: LocalDate,
    )
}
