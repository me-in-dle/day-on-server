package com.day.on.location.usecase.inbound

import com.day.on.location.usecase.dto.DistrictOperationResult

/**
 * 행정구역 UseCase (인바운드 포트)
 */
interface DistrictUseCase {
    
    /**
     * 행정구역 데이터 저장
     */
    fun savePreprocessedDistricts(jsonFilePath: String): DistrictOperationResult
    
    /**
     * 행정구역 데이터 삭제
     */
    fun deleteAllDistricts(confirm: String)

}
