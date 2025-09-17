package com.day.on.calendar.adapter

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.repository.DailyScheduleJpaRepository
import com.day.on.calendar.repository.ScheduleContentJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarEventQueryPort
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
@Transactional(readOnly = true)
class CalendarEventQueryAdapter(
        private val dailyScheduleJpaRepository: DailyScheduleJpaRepository,
        private val scheduleContentJpaRepository: ScheduleContentJpaRepository
) : CalendarEventQueryPort {

    override fun findByDate(accountId : Long, date : LocalDate) : List<ScheduleContent> {
        val dailySchedule = dailyScheduleJpaRepository.findByAccountIdAndDay(accountId, date)?: return emptyList()
        // TODO : 예외처리는 서비스단에서.
        return scheduleContentJpaRepository.findByDailySchedulesId(dailySchedule.id)
                .map { it.toDomain() }
    }
}