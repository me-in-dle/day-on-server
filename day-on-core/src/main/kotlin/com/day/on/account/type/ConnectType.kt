package com.day.on.account.type

enum class ConnectType(
    val connectTypeName: String,
) {
    KAKAO("kakao"),
    GOOGLE("google");

    companion object {
        fun matchConnectType(codeName: String): ConnectType {
            return when (codeName) {
                GOOGLE.connectTypeName -> GOOGLE
                KAKAO.connectTypeName -> KAKAO
                else -> throw IllegalArgumentException("Invalid ConnectType: $codeName")
            }
        }
    }
}