package com.day.on.scheduler

import com.day.on.websocket.usecase.inbound.LifePatternReminderUseCase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LifePatternReminderScheduler (private val reminderUseCase: LifePatternReminderUseCase) {
    @Scheduled(cron = "0 0 7 * * *")
    fun sendSleepReminder() {
        reminderUseCase.remindSleep()
    }
    // 매일 오후 10시에 실행
    @Scheduled(cron = "0 0 22 * * *")
    fun sendStepReminder() {
        reminderUseCase.remindSteps()
    }
}