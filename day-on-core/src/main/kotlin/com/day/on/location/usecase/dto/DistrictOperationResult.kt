package com.day.on.location.usecase.dto

/**
 * 행정구역 데이터 관리 작업 결과 응답 DTO
 */
data class DistrictOperationResult(
    val savedCount: Int,
    val message: String
)
