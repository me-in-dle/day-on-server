package com.day.on.common.exception

/**
 * 비즈니스 예외 기본 클래스
 * 모든 도메인별 예외가 상속받는 베이스 클래스
 */
open class BusinessException(
    val errorCode: CommonErrorCode,
    message: String
) : RuntimeException(message) {

    /**
     * 메시지 포맷팅을 지원하는 생성자
     * 예: "사용자 %s를 찾을 수 없습니다", "user123"
     */
    constructor(
        errorCode: String,
        message: String,
        vararg args: Any
    ) : this(
        object : CommonErrorCode {
            override val errorCode: String = errorCode
            override val message: String = formatMessage(message, *args)
        },
        formatMessage(message, *args)
    )

    companion object {
        private fun formatMessage(message: String, vararg args: Any): String {
            return if (args.isEmpty()) message else message.format(*args)
        }
    }
}
