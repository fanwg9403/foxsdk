package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 37 分
 * @Desc TODO:
 */
class FSHomeRepository : FoxSdkBaseRepository() {

    private val service: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    suspend fun loginByVerifyCode(phone: String, verifyCode: String) = executeCall {
        service.login(
            mapOf(
                "channel_id" to WishFoxSdk.getConfig().channelId,
                "app_id" to WishFoxSdk.getConfig().appId,
                "user_name" to phone,
                "code" to verifyCode,
                "login_type" to "2"
            )
        )
    }

    suspend fun loginByPassword(phone: String, password: String) = executeCall {
        service.login(
            mapOf(
                "channel_id" to WishFoxSdk.getConfig().channelId,
                "app_id" to WishFoxSdk.getConfig().appId,
                "user_name" to phone,
                "pass_word" to password,
                "login_type" to "1"
            )
        )
    }

    suspend fun getUserInfo() = executeCall {
        service.getUserInfo(
            mapOf(
                "channel_id" to WishFoxSdk.getConfig().channelId,
                "app_id" to WishFoxSdk.getConfig().appId
            )
        )
    }

    suspend fun logout() = executeCall {
        service.logout()
    }

    suspend fun getUserVirtualInfo() = executeCall {
        service.getUserVirtualInfo()
    }

    suspend fun getAdvertiseList() = executeCall {
        service.getAdvertiseList(1, 10, "app-yx-grzx")
    }
}