package com.sohuglobal.foxsdk.data.repository

import com.google.gson.Gson
import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.network.FoxSdkNetworkExecutor
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 *
 * 主要功能: MVI架构下的BaseRepository
 * @Description: 职责：纯粹的数据获取和协调，不包含状态管理
 * @author: 范为广
 * @date: 2025年10月11日 17:53
 */
abstract class FoxSdkBaseRepository {

    /**
     * 执行普通网络请求
     */
    protected suspend fun <T> executeCall(
        showLoading: Boolean = false,
        call: suspend () -> FoxSdkBaseResponse<T>
    ): FoxSdkNetworkResult<T> {
        return FoxSdkNetworkExecutor.execute(call, showLoading)
    }

    /**
     * 执行网络请求并返回Flow
     */
    protected fun <T> executeCallAsFlow(
        showLoading: Boolean = false,
        call: suspend () -> FoxSdkBaseResponse<T>
    ): Flow<FoxSdkNetworkResult<T>> {
        return FoxSdkNetworkExecutor.executeAsFlow(call, showLoading)
    }

    /**
     * 执行分页网络请求（简单列表）
     */
    protected fun <T> executePageCall(
        pageRequest: FoxSdkPageRequest,
        apiCall: suspend (Int, Int) -> FoxSdkBaseResponse<List<T>>
    ): Flow<FoxSdkNetworkResult<List<T>>> {
        return FoxSdkNetworkExecutor.executePage(pageRequest, apiCall)
    }

    /**
     * 执行带缓存的分页网络请求
     */
    protected fun <T> executePageCallWithCache(
        pageRequest: FoxSdkPageRequest,
        cacheKey: String,
        cacheProvider: suspend (String, Int, Int) -> List<T>?,
        networkCall: suspend (Int, Int) -> FoxSdkBaseResponse<List<T>>,
        cacheUpdater: suspend (String, Int, List<T>) -> Unit
    ): Flow<FoxSdkNetworkResult<List<T>>> {
        return FoxSdkNetworkExecutor.executePageWithCache(
            pageRequest,
            cacheKey,
            cacheProvider,
            networkCall,
            cacheUpdater
        )
    }

    /**
     * 执行带缓存的普通网络请求
     */
    protected suspend fun <T> executeCachedCall(
        cacheKey: String,
        shouldRefresh: Boolean = false,
        cacheProvider: suspend (String) -> T?,
        networkCall: suspend () -> FoxSdkBaseResponse<T>,
        cacheUpdater: suspend (String, T) -> Unit
    ): FoxSdkNetworkResult<T> {
        return FoxSdkNetworkExecutor.executeWithCache(
            cacheKey,
            shouldRefresh,
            cacheProvider,
            networkCall,
            cacheUpdater
        )
    }

    fun Map<*, *>.toBody(mediaType: String = "application/json") =
        Gson().toJson(this).toRequestBody(mediaType.toMediaType())
}