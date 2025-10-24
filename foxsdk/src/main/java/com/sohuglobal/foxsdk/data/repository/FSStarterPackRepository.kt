package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @file FSStarterPackRepository
 * 文件说明：新手礼包数据仓库
 *
 * @author 王金强
 * @date 2025/10/13 15:50
 */
class FSStarterPackRepository : FoxSdkBaseRepository() {

    private val apiService: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    /**
     * 获取新手礼包列表
     */
    suspend fun getStarterPackList(
        page: Int,
        size: Int,
        mail_title: String? = "",
    ): FoxSdkBaseResponse<FSPageContainer<FSStarterPack>> {
        val params = mutableMapOf<String, Any>()
        params["page_num"] = page
        params["page_size"] = size
       if(!mail_title.isNullOrEmpty()){
           params["mail_title"] = mail_title
       }
        return  apiService.getStarterPacks(params)
    }

    /**
     * 领取新手礼包
     */
    suspend fun receiveStarterPack(mail_id: String): FoxSdkBaseResponse<Any>{
        val params = mutableMapOf<String, String>()
        params["mail_id"] = mail_id
        params["channel_id"] = WishFoxSdk.getConfig().channelId
        params["app_id"] = WishFoxSdk.getConfig().appId
        return apiService.receiveStarterPack(params)
    }
}