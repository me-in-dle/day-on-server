package com.day.on.api.exception

import com.day.on.common.exception.CommonErrorCode

data class ApiDefaultErrorCode(
    override val errorCode: String,
    override val message: String
) : CommonErrorCode