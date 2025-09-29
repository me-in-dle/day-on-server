package com.day.on.location.endpoint

import com.day.on.api.response.SuccessResponse
import com.day.on.location.dto.DistrictDeleteRequest
import com.day.on.location.usecase.dto.DistrictOperationResponse
import com.day.on.location.usecase.dto.DistrictSearchResponse
import com.day.on.location.usecase.inbound.DistrictUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 행정구역 Controller
 */
@RestController
@RequestMapping("/api/v1/districts")
class DistrictController(
    private val districtDataUseCase: DistrictUseCase
) {
    
    /**
     * 행정구역 데이터 저장
     *
     * @param jsonFilePath JSON 파일의 절대 경로 또는 상대 경로 (korean_administrative_districts_preprocessed.json)
     * @return 저장 결과 정보 (저장된 개수, 메시지)
     */
    @PostMapping("/save-preprocessed")
    @ResponseStatus(HttpStatus.CREATED)
    fun savePreprocessedDistricts(@RequestParam jsonFilePath: String): SuccessResponse<DistrictOperationResponse> {
        val response = districtDataUseCase.savePreprocessedDistricts(jsonFilePath)
        return SuccessResponse.of(response)
    }
    
    /**
     * 행정구역 데이터 삭제
     * @param request 삭제 확인 요청 (confirm 필드에 "DELETE_ALL" 필요)
     */
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAllDistricts(@RequestBody @Valid request: DistrictDeleteRequest) {
        districtDataUseCase.deleteAllDistricts(request.confirm)
    }

    /**
     * GPS 좌표로 행정구역 검색
     */
    @GetMapping("/search")
    fun searchDistrict(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): SuccessResponse<DistrictSearchResponse> {
        val response = districtDataUseCase.findDistrictByCoordinates(latitude, longitude)
        return SuccessResponse.of(response)
    }

}
