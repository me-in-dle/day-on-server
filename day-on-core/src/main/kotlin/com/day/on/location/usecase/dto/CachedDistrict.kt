package com.day.on.location.usecase.dto

/**
 * 캐시용 데이터 (메모리에 250개 저장 후 반복 사용)
 * 1. 하버사인 거리 계산
 * 2. Ray Casting 알고리즘 사용
 */
data class CachedDistrict(
    val sigCd: String,
    val sigKorNm: String,
    val sidoNm: String,
    val districtFullNm: String,
    val centerLatitude: Double,     // 하버사인 계산용
    val centerLongitude: Double,    // 하버사인 계산용
    val coordinates: String         // Ray Casting용
)