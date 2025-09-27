package com.day.on.location.exception

import com.day.on.common.exception.BusinessException

/**
 * 위치 도메인 예외
 */
class LocationException(
    errorCodeEnum: LocationErrorCode,
    vararg args: Any
) : BusinessException(
    errorCodeEnum.errorCode,
    errorCodeEnum.message,
    *args
)
