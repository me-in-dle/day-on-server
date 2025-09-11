package com.day.on.account.exception

import org.springframework.http.HttpStatus

/**
 * 계정 도메인 에러 코드
 */
enum class AccountErrorCode(
    val httpStatus: HttpStatus,
    val errorCode: String,
    val message: String
) {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "4001", "계정을 찾을 수 없습니다"),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "4002", "이미 존재하는 계정입니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "4004", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4003", "토큰이 만료되었습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "4005", "비밀번호가 올바르지 않습니다"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "4006", "비활성화된 계정입니다"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "4007", "이메일 형식이 올바르지 않습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "4008", "이미 사용 중인 이메일입니다");
}
