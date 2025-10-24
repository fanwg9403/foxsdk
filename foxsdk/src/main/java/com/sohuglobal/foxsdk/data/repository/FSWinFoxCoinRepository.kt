package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSWinFoxCoin
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager

/**
 * @file FSWinFoxCoinRepository
 * 文件说明：赢狐币数据仓库
 *
 * @author 王金强
 * @date 2025/10/14 14:46
 */
class FSWinFoxCoinRepository: FoxSdkBaseRepository() {
    private val apiService: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    /**
     * 获取新手礼包列表
     */
    suspend fun getWinFoxCoinList(
        page: Int,
        size: Int
    ): FoxSdkBaseResponse<List<FSWinFoxCoin>> {
        return  apiService.getWinFoxCoins(page,size)
    }
}