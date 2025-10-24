package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSGameRecord
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @file FSGameRecordRepository
 * 文件说明：游戏记录数据仓库
 *
 * @author 王金强
 * @date 2025/10/17 9:51
 */
class FSGameRecordRepository {

    private val apiService: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    /**
     * 获取游戏记录列表
     */
    suspend fun getGameRecordList(
        page: Int,
        size: Int,
        channelId: String,
        appId: String
    ): FoxSdkBaseResponse<FSPageContainer<FSGameRecord>> {
        return apiService.getGameRecordList(page, size,channelId,appId)
    }
}