package com.day.on.common.response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * API 실패 응답 모델
 * 
 * @property success 성공 여부 (항상 false)
 * @property message 에러 메시지
 * @property errorCode 에러 코드 (4001, 5001 등)
 * @property path 요청 경로
 * @property timestamp 응답 시각 (ISO 8601 형식)
 */
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errorCode: String,
    val path: String,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
) {
    companion object {
        /**
         * 에러 응답 생성
         */
        fun of(
            message: String,
            errorCode: String,
            path: String
        ): ErrorResponse {
            return ErrorResponse(
                message = message,
                errorCode = errorCode,
                path = path
            )
        }
    }
}
