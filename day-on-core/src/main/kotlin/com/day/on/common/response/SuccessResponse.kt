package com.day.on.common.response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * API 성공 응답 모델
 * 
 * @param T 응답 데이터 타입
 * @property success 성공 여부 (항상 true)
 * @property data 응답 데이터
 * @property timestamp 응답 시각 (ISO 8601 형식)
 */
data class SuccessResponse<T>(
    val success: Boolean = true,
    val data: T,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
) {
    companion object {
        /**
         * 성공 응답 생성
         */
        fun <T> of(data: T): SuccessResponse<T> {
            return SuccessResponse(data = data)
        }
    }
}
