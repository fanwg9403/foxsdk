package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.model.entity.FSRechargeRecord
import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @file FSRechargeRecordRepository
 * 文件说明：充值记录数据仓库
 *
 * @author 王金强
 * @date 2025/10/16 18:34
 */
class FSRechargeRecordRepository : FoxSdkBaseRepository() {

    private val apiService: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    /**
     * 获取充值记录列表
     */
    suspend fun getRechargeRecords(page: Int, size: Int) :FoxSdkBaseResponse<FSPageContainer<FSRechargeRecord>> {
        return apiService.getRechargeRecords(page, size)
    }
}