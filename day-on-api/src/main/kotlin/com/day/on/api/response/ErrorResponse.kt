package com.day.on.api.response

import com.day.on.api.exception.ApiDefaultErrorCode
import com.day.on.common.exception.CommonErrorCode
import java.lang.Exception
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
    val message: String?,
    val errorCode: CommonErrorCode,
    val path: String,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
) {
    companion object {
        /**
         * 에러 응답 생성
         */
        fun of(
            message: String?,
            errorCode: CommonErrorCode,
            path: String
        ): ErrorResponse {
            return ErrorResponse(
                message = message,
                errorCode = errorCode,
                path = path
            )
        }

        fun of(
            message: String?,
            ex: Exception,
            path: String
        ): ErrorResponse {
            return ErrorResponse(
                message = message,
                errorCode = ApiDefaultErrorCode(
                    errorCode = ex.javaClass.simpleName,
                    message = ex.message?.substring(50) ?: "서버 에러"
                ),
                path = path
            )
        }
    }
}
