package com.day.on.account.exception

import com.day.on.common.exception.CommonErrorCode

/**
 * 계정 도메인 에러 코드
 */
enum class AccountErrorCode(
    override val errorCode: String,
    override val message: String
) : CommonErrorCode {
    ACCOUNT_NOT_FOUND("4001", "계정을 찾을 수 없습니다"),
    ACCOUNT_ALREADY_EXISTS("4002", "이미 존재하는 계정입니다"),
    INVALID_TOKEN("4004", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED("4003", "토큰이 만료되었습니다"),
    INVALID_PASSWORD("4005", "비밀번호가 올바르지 않습니다"),
    ACCOUNT_DISABLED("4006", "비활성화된 계정입니다"),
    INVALID_EMAIL_FORMAT("4007", "이메일 형식이 올바르지 않습니다"),
    EMAIL_ALREADY_EXISTS("4008", "이미 사용 중인 이메일입니다");
}
