package com.day.on.location.usecase.dto

/**
 * 행정구역 검색 결과
 * 1. 날씨 서비스
 * 2. 다른 위치 기반 서비스
 * 3. 사용자 현재 위치 표시
 */
data class DistrictSearchResponse(
    val sigCd: String,              // 행정표준코드(법정동코드) ex. 11440
    val sidoNm: String,             // 상위 시도명 ex. 서울특별시
    val sigKorNm: String,           // 행정구역명 ex. 마포구
    val districtFullNm: String,     // 전체 구역명 ex. 서울특별시 마포구
    val centerLatitude: Double,     // 중심 위도
    val centerLongitude: Double,    // 중심 경도
    val distance: Double,           // 사용자로부터의 거리(km)
    val isExactMatch: Boolean       // 정확히 구역 내부에 있는지 여부
)