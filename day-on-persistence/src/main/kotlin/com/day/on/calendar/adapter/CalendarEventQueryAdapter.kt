package com.day.on.calendar.adapter

import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.repository.DailyScheduleJpaRepository
import com.day.on.calendar.repository.ScheduleContentJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarEventQueryPort
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import java.time.LocalDate


@Repository
@Transactional(readOnly = true)
class CalendarEventQueryAdapter(
        private val dailyScheduleJpaRepository: DailyScheduleJpaRepository,
        private val scheduleContentJpaRepository: ScheduleContentJpaRepository,
) : CalendarEventQueryPort {


    override fun findByDate(accountId : Long, date : LocalDate) : List<ScheduleContent> {
        val dailySchedule = dailyScheduleJpaRepository.findByAccountIdAndDay(accountId, date)?: return emptyList()
        // TODO : 예외처리는 서비스단에서.
        return scheduleContentJpaRepository.findByDailySchedulesId(dailySchedule.id)
                .map { it.toDomain() }
    }

    override fun findMissingDays(accountId: Long, start: LocalDate, end: LocalDate): List<LocalDate> {
        val existingDays = dailyScheduleJpaRepository.findByAccountIdAndDayBetween(accountId, start, end)
                .map { it.day }
                .toSet()

        return generateSequence(start) { it.plusDays(1) }
                .takeWhile { !it.isAfter(end) }
                .filterNot { it in existingDays }
                .toList()
    }

}