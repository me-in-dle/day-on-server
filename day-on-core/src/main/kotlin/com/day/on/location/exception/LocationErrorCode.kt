package com.day.on.location.exception

import com.day.on.common.exception.CommonErrorCode

/**
 * 위치 도메인 에러 코드
 */
enum class LocationErrorCode(
    override val errorCode: String,
    override val message: String
) : CommonErrorCode {
    DISTRICT_DATA_ALREADY_EXISTS("4201", "행정구역 데이터가 이미 존재합니다. 기존 데이터를 삭제 후 다시 시도해주세요."),
    DISTRICT_JSON_FILE_NOT_FOUND("4202", "JSON 파일을 처리할 수 없습니다.")
}
