package com.day.on.location.service

import com.day.on.location.usecase.dto.DistrictOperationResult
import com.day.on.location.usecase.inbound.DistrictUseCase
import com.day.on.location.usecase.outbound.DistrictPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 행정구역 UseCase 구현체 (인바운드 어댑터)
 */
@Service
@Transactional(readOnly = true)
class DistrictService(
    private val districtDataPort: DistrictPort
) : DistrictUseCase {

    @Transactional
    override fun savePreprocessedDistricts(jsonFilePath: String): DistrictOperationResult {
        val savedCount = districtDataPort.savePreprocessedDistricts(jsonFilePath)
        return DistrictOperationResult(
            savedCount = savedCount,
            message = "행정구역 데이터 저장 완료"
        )
    }

    @Transactional
    override fun deleteAllDistricts(confirm: String) {
        districtDataPort.deleteAllDistricts()
    }

}
