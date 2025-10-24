package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 09点 20 分
 * @Desc TODO:
 */
class FSMessageRepository : FoxSdkBaseRepository() {
    private val service: FoxSdkApiService by lazy { FoxSdkRetrofitManager.getApiService() }

    private var _pageSize = 10
    val pageSize: Int = _pageSize
    fun setPageSize(pageSize: Int) {
        _pageSize = pageSize
    }

    suspend fun getMessageList(page: Int, size: Int, channelId: String, appId: String) =
        executeCall {
            service.getSystemMessages(
                page = page,
                pageSize = size,
                channelId = channelId,
                appId = appId,
                type = 1
            )
        }

    suspend fun read(id: Long? = null) = executeCall {
        if (id != null) {
            val body = mapOf(
                "mail_id" to id,
                "channel_id" to WishFoxSdk.getConfig().channelId,
                "app_id" to WishFoxSdk.getConfig().appId
            ).toBody()
            service.read(body)
        } else {
            service.read(WishFoxSdk.getConfig().channelId, WishFoxSdk.getConfig().appId)
        }
    }
}