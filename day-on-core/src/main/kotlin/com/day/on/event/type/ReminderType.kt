package com.day.on.event.type

enum class ReminderType(val meta: String, val message: String) {
    STEP("REMINDER_STEP", "오늘 얼마나 걸으셨나요? 걸음 수를 기록해보세요!"),
    SLEEP("REMINDER_SLEEP", "어젯밤에 얼마나 주무셨나요? 수면 시간을 기록해보세요!")
}