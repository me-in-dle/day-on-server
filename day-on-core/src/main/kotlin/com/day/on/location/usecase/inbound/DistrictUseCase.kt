package com.day.on.location.usecase.inbound

import com.day.on.location.usecase.dto.DistrictOperationResponse
import com.day.on.location.usecase.dto.DistrictSearchResponse

/**
 * 행정구역 UseCase (인바운드 포트)
 */
interface DistrictUseCase {
    
    /**
     * 행정구역 데이터 저장
     */
    fun savePreprocessedDistricts(jsonFilePath: String): DistrictOperationResponse
    
    /**
     * 행정구역 데이터 삭제
     */
    fun deleteAllDistricts(confirm: String)

    /**
     * GPS 좌표로 해당 행정구역 검색
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 사용자가 위치한 행정구역 (1개)
     */
    fun findDistrictByCoordinates(latitude: Double, longitude: Double): DistrictSearchResponse

}
