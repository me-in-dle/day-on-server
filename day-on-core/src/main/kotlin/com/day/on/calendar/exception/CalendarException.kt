package com.day.on.calendar.exception

import com.day.on.account.exception.AccountErrorCode
import com.day.on.common.exception.BusinessException

class CalendarException(
    errorCodeEnum: AccountErrorCode,
    vararg args: Any
) : BusinessException(
    errorCodeEnum.errorCode,
    errorCodeEnum.message,
    *args
)