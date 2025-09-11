package com.day.on.account.exception

import com.day.on.common.exception.BusinessException

/**
 * 계정 도메인 예외
 */
class AccountException(
    private val errorCodeEnum: AccountErrorCode,
    vararg args: Any
) : BusinessException(
    errorCodeEnum.httpStatus,
    errorCodeEnum.errorCode,
    errorCodeEnum.message,
    *args
)
