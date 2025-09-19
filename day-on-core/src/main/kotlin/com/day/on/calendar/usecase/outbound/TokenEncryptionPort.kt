package com.day.on.calendar.usecase.outbound

interface TokenEncryptionPort {
    fun encrypt(plainText: String): String
    fun decrypt(encryptedText: String): String
}