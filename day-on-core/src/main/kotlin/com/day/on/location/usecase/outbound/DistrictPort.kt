package com.day.on.location.usecase.outbound

/**
 * 행정구역 Port (아웃바운드 포트)
 */
interface DistrictPort {
    
    /**
     * 행정구역 데이터 저장
     */
    fun savePreprocessedDistricts(jsonFilePath: String): Int
    
    /**
     * 행정구역 데이터 삭제
     */
    fun deleteAllDistricts()

}
