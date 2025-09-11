package com.day.on.common.exception

import org.springframework.http.HttpStatus

/**
 * 에러 코드 정의
 * 
 * @property code 에러 코드 (4001, 5001 등)
 * @property message 기본 에러 메시지
 * @property httpStatus HTTP 상태 코드
 */
enum class ErrorCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus
) {
    // 4001~4099: 계정/인증 관련
    ACCOUNT_NOT_FOUND("4001", "계정을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    ACCOUNT_ALREADY_EXISTS("4002", "이미 존재하는 계정입니다", HttpStatus.CONFLICT),
    TOKEN_EXPIRED("4003", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("4004", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    
    // 4101~4199: 입력값 검증 오류
    MISSING_PARAMETER("4101", "필수 파라미터가 누락되었습니다", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("4102", "이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_TYPE("4103", "파라미터 타입이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("4104", "날짜 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    
    // 4201~4299: 권한/접근 오류
    ACCESS_DENIED("4201", "해당 리소스에 접근할 권한이 없습니다", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("4202", "비활성화된 계정입니다", HttpStatus.FORBIDDEN),
    
    // 4301~4399: HTTP 요청 오류
    METHOD_NOT_ALLOWED("4301", "지원하지 않는 HTTP 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE("4302", "지원하지 않는 Content-Type입니다", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FILE_SIZE_EXCEEDED("4303", "파일 크기가 제한을 초과했습니다", HttpStatus.PAYLOAD_TOO_LARGE),
    
    // 5001~5099: 외부 API 연동 오류
    GOOGLE_API_ERROR("5001", "Google API 호출에 실패했습니다", HttpStatus.BAD_GATEWAY),
    CLAUDE_API_ERROR("5002", "Claude AI API 호출에 실패했습니다", HttpStatus.BAD_GATEWAY),
    WEATHER_API_ERROR("5003", "날씨 API 호출에 실패했습니다", HttpStatus.BAD_GATEWAY),
    EXTERNAL_API_TIMEOUT("5004", "외부 서비스 응답 시간을 초과했습니다", HttpStatus.GATEWAY_TIMEOUT),
    
    // 5101~5199: 데이터베이스 오류
    DATABASE_CONNECTION_ERROR("5101", "데이터베이스 연결에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_QUERY_ERROR("5102", "데이터 조회 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_TRANSACTION_ERROR("5103", "데이터 저장 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 5201~5299: 서버 내부 오류
    INTERNAL_SERVER_ERROR("5201", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_RESOURCE_ERROR("5202", "서버 리소스가 부족합니다", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_CONFIG_ERROR("5203", "서버 설정 오류입니다", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 5301~5399: 비즈니스 로직 오류
    RECOMMENDATION_GENERATION_ERROR("5301", "추천 생성 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    SCHEDULE_SYNC_ERROR("5302", "일정 동기화 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
}
