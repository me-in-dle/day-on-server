package com.day.on.recommend.type

enum class FeedbackAction {
    IMPRESSION,     // 노출됨
    VIEW,           // 상세 보기
    SELECT,         // 선택(확정)
    DISMISS,        // 스킵/미선택
    LIKE,           // 선호 표시
    DISLIKE         // 비선호 표시
    ;

    companion object {
        private val map = entries.associateBy(FeedbackAction::name)
        fun from(value: String) = map[value] ?: throw IllegalArgumentException("Invalid FeedbackAction value: $value")
    }
}