package com.day.on.location.dto

import jakarta.validation.constraints.Pattern

/**
 * 행정구역 데이터 삭제 HTTP Request Dto
 */
data class DistrictDeleteRequest(
    @field:Pattern(
        regexp = "DELETE_ALL", 
        message = "삭제 확인을 위해 confirm=DELETE_ALL 파라미터가 필요합니다"
    )
    val confirm: String
)