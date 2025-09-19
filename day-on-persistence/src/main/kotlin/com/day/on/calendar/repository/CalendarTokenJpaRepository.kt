package com.day.on.calendar.repository

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.CalendarTokensEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarTokenJpaRepository : JpaRepository<CalendarTokensEntity, Long> {
    fun findByAccountIdAndConnectType(accountId: Long, connectType: ConnectType): CalendarTokensEntity?

}