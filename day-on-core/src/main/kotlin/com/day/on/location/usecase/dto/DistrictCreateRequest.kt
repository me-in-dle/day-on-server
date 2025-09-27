package com.day.on.location.usecase.dto

import java.math.BigDecimal

/**
 * 행정구역 생성 Request Dto
 * JSON 파일에서 읽은 데이터를 담는 Dto
 */
data class DistrictCreateRequest(
    val sigCd: String,
    val sigKorNm: String,
    val geometryType: String,
    val coordinates: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val sidoNm: String,
    val districtFullNm: String
) {

    /**
     * 데이터 유효성 검증
     */
    fun validate() {
        require(sigCd.isNotBlank()) { "행정표준코드(법정동코드)는 필수입니다" }
        require(sigKorNm.isNotBlank()) { "행정구역명은 필수입니다" }
        require(geometryType.isNotBlank()) { "GeoJSON Geometry 타입은 필수입니다" }
        require(coordinates.isNotBlank()) { "행정구역 경계선 좌표는 필수입니다" }
        require(centerLatitude in 33.0..39.0) { "행정구역 중심점 위도는 33.0~39.0 범위여야 합니다" }
        require(centerLongitude in 124.0..132.0) { "행정구역 중심점 경도는 124.0~132.0 범위여야 합니다" }
        require(sidoNm.isNotBlank()) { "상위 시도명은 필수입니다" }
        require(districtFullNm.isNotBlank()) { "전체 구역명은 필수입니다" }
    }

    /**
     * BigDecimal 변환된 위도
     */
    val centerLatitudeBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(centerLatitude)

    /**
     * BigDecimal 변환된 경도
     */
    val centerLongitudeBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(centerLongitude)

}