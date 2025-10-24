package com.sohuglobal.foxsdk.data.repository

import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import kotlinx.coroutines.flow.Flow

/**
 *
 * 主要功能: 用户数据仓库
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 18:30
 */
class FSUserRepository : FoxSdkBaseRepository() {

    private val apiService: FoxSdkApiService by lazy {
        FoxSdkRetrofitManager.getApiService()
    }

    // 内存缓存
    private val userCache = mutableMapOf<String, FSUserInfo>()
    private val pageUserFollowCache = mutableMapOf<String, MutableMap<Int, List<FSUserInfo>>>()

    /**
     * 获取用户信息
     */
    suspend fun getUserProfile(): FoxSdkNetworkResult<FSUserInfo> {
        return executeCall {
            apiService.getUserProfile()
        }
    }

    /**
     * 获取用户信息（带缓存）
     */
    suspend fun getUserProfileWithCache(
        userId: String,
        forceRefresh: Boolean = false
    ): FoxSdkNetworkResult<FSUserInfo> {
        return executeCachedCall(
            cacheKey = "user_$userId",
            shouldRefresh = forceRefresh,
            cacheProvider = { key -> userCache[key] },
            networkCall = { apiService.getUserProfile() },
            cacheUpdater = { key, data -> userCache[key] = data }
        )
    }

    /**
     * 获取分页用户关注列表
     */
    suspend fun getUserFollows(
        userId: String,
        page: Int,
        size: Int
    ): FoxSdkBaseResponse<List<FSUserInfo>> {
        return apiService.getUserFollowings(userId, page, size)
    }

    /**
     * 获取分页用户关注列表（带缓存）
     */
    fun getUserFollowsWithCache(
        userId: String,
        pageRequest: FoxSdkPageRequest
    ): Flow<FoxSdkNetworkResult<List<FSUserInfo>>> {
        val cacheKey = "users_all"

        return executePageCallWithCache(
            pageRequest = pageRequest,
            cacheKey = cacheKey,
            cacheProvider = { key, page, pageSize ->
                pageUserFollowCache[key]?.get(page)
            },
            networkCall = { page, pageSize ->
                apiService.getUserFollowings(userId, page, pageSize)
            },
            cacheUpdater = { key, page, data ->
                val pageCache = pageUserFollowCache.getOrPut(key) { mutableMapOf() }
                pageCache[page] = data
            }
        )
    }

    /**
     * 清除缓存
     */
    fun clearCache(userId: String? = null) {
        if (userId != null) {
            userCache.remove("user_$userId")
        } else {
            userCache.clear()
            pageUserFollowCache.clear()
        }
    }
}