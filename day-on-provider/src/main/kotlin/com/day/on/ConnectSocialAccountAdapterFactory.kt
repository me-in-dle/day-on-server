package com.day.on

import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.outbound.ConnectSocialAccountPort
import com.day.on.adapter.ConnectSocialAccountGoogleAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Qualifier("userAccountSocialSourceAdapterFactory")
@Component
class ConnectSocialAccountAdapterFactory(
    private val connectSocialAccountGoogleAdapter: ConnectSocialAccountGoogleAdapter,
) : ConnectSocialAccountPort {
    override fun connect(code: String, connectType: ConnectType): ConnectAccount {
        return when (connectType) {
            ConnectType.GOOGLE -> connectSocialAccountGoogleAdapter.connect(code, connectType)
            ConnectType.KAKAO -> throw NotImplementedError("KAKAO is not implemented yet")
        }
    }
}