package com.day.on.websocket.service

import com.day.on.account.usecase.outbound.AccountQueryPort
import com.day.on.common.outbound.LockManager
import com.day.on.event.type.ReminderType
import com.day.on.websocket.usecase.inbound.LifePatternReminderUseCase
import com.day.on.websocket.usecase.outbound.LifePatternCommandPort
import com.day.on.websocket.usecase.outbound.MessageDispatchPort
import com.day.on.websocket.usecase.outbound.NotificationPort
import org.springframework.stereotype.Service

@Service
class LifePatternReminderService (
        private val dispatchPort: MessageDispatchPort,
        // private val notifyPort: NotificationPort,
        private val logPort: LifePatternCommandPort,
        private val lockManager: LockManager,
        private val accountQueryPort: AccountQueryPort
) : LifePatternReminderUseCase {

    override fun remindSteps() {
        lockManager.lock("REMINDER_STEP_LOCK") {
            val users = accountQueryPort.findAll()
            val (meta, message) = ReminderType.SLEEP.let { it.meta to it.message }

            users.forEach { user ->
                dispatchPort.dispatchBroadcast(message)
                // notifyPort.sendToKakao(user.id, message)
                logPort.saveReminderLog(user.id, meta, message)
            }
        }
    }

    override fun remindSleep() {
        lockManager.lock("REMINDER_SLEEP_LOCK") {
            val users = accountQueryPort.findAll()
            val (meta, message) = ReminderType.SLEEP.let { it.meta to it.message }

            users.forEach { user ->
                dispatchPort.dispatchBroadcast(message)
                // notifyPort.sendToKakao(user.id, message)
                logPort.saveReminderLog(user.id, meta, message)
            }
        }
    }

}