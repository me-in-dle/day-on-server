package com.day.on.account.usecase.outbound

import com.day.on.account.model.Account

interface AccountQueryPort {
    fun findById(id: Long): Account
}