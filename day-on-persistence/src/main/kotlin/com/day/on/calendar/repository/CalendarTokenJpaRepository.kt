package com.day.on.calendar.repository

import com.day.on.calendar.jpa.CalendarTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarTokenJpaRepository : JpaRepository<CalendarTokenEntity, Long> {

}