package com.sohuglobal.foxsdk.data.network

import android.net.ParseException
import android.util.Log
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException

/**
 *
 * 主要功能: 统一的网络请求执行器
 * @Description: 支持普通请求和分页请求
 * @author: 范为广
 * @date: 2025年10月12日 9:50
 */
object FoxSdkNetworkExecutor {

    private val TAG = "FoxSdk[Executor]"

    /**
     * 执行普通网络请求
     */
    suspend fun <T> execute(
        call: suspend () -> FoxSdkBaseResponse<T>,
        showLoading: Boolean = false
    ): FoxSdkNetworkResult<T> {
        return try {
            if (showLoading) {
                FoxSdkNetworkResult.Loading()
            }

            val response = call()

            when {
                response.isSuccess && response.data != null -> {
                    FoxSdkNetworkResult.Success(response.data, response.message)
                }

                response.isSuccess && response.data == null -> {
                    FoxSdkNetworkResult.Empty(response.message ?: "数据为空")
                }

                else -> {
                    FoxSdkNetworkResult.Error(
                        error = response.message ?: "请求失败",
                        code = response.code
                    )
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * 执行网络请求并返回Flow
     */
    fun <T> executeAsFlow(
        call: suspend () -> FoxSdkBaseResponse<T>,
        showLoading: Boolean = true
    ): Flow<FoxSdkNetworkResult<T>> = flow {
        if (showLoading) {
            emit(FoxSdkNetworkResult.Loading())
        }

        try {
            val response = call()

            val result = when {
                response.isSuccess && response.data != null -> {
                    FoxSdkNetworkResult.Success(response.data, response.message)
                }

                response.isSuccess && response.data == null -> {
                    FoxSdkNetworkResult.Empty(response.message ?: "数据为空")
                }

                else -> {
                    FoxSdkNetworkResult.Error(
                        error = response.message ?: "请求失败",
                        code = response.code
                    )
                }
            }
            emit(result)
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

    /**
     * 执行分页网络请求
     * 现在API直接返回 List<T>，分页信息通过参数传递
     */
    fun <T> executePage(
        pageRequest: FoxSdkPageRequest,
        apiCall: suspend (Int, Int) -> FoxSdkBaseResponse<List<T>>
    ): Flow<FoxSdkNetworkResult<List<T>>> = flow {
        emit(
            FoxSdkNetworkResult.PageLoading(
                isInitial = pageRequest.isInitial,
                page = pageRequest.page
            )
        )

        try {
            val response = apiCall(pageRequest.page, pageRequest.pageSize)

            when {
                response.isSuccess && response.data != null -> {
                    val data = response.data
                    // 判断是否还有更多数据：如果返回的数据量等于请求的页面大小，则认为还有更多数据
                    val hasMore = data.size >= pageRequest.pageSize

                    emit(
                        FoxSdkNetworkResult.PageSuccess(
                            data = data,
                            page = pageRequest.page,
                            hasMore = hasMore,
                            totalCount = response.total // 如果API不返回总数量，可以设为null
                        )
                    )
                }

                response.isSuccess && response.data == null -> {
                    emit(FoxSdkNetworkResult.Empty("暂无数据"))
                }

                else -> {
                    emit(
                        FoxSdkNetworkResult.PageError(
                            error = response.message ?: "请求失败",
                            page = pageRequest.page,
                            code = response.code
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(handlePageException(e, pageRequest.page))
        }
    }



    /**
     * 执行分页网络请求返回对象
     * 现在API直接返回 List<T>，分页信息通过参数传递
     */
    fun <T> executePageData(
        pageRequest: FoxSdkPageRequest,
        apiCall: suspend (Int, Int) -> FoxSdkBaseResponse<T>
    ): Flow<FoxSdkNetworkResult<T>> = flow {
        emit(
            FoxSdkNetworkResult.PageLoading(
                isInitial = pageRequest.isInitial,
                page = pageRequest.page
            )
        )

        try {
            val response = apiCall(pageRequest.page, pageRequest.pageSize)

            when {
                response.isSuccess && response.data != null -> {
                    val data = response.data
                    // 判断是否还有更多数据：如果返回的数据量等于请求的页面大小，则认为还有更多数据
                    val hasMore = when(data){
                        is FSPageContainer<*>-> {
                            (data.data?.size ?: 0) >= pageRequest.pageSize
                        }
                        else -> true

                    }
                    emit(
                        FoxSdkNetworkResult.PageSuccess(
                            data = data,
                            page = pageRequest.page,
                            hasMore = hasMore,
                            totalCount = response.total // 如果API不返回总数量，可以设为null
                        )
                    )
                }

                response.isSuccess && response.data == null -> {
                    emit(FoxSdkNetworkResult.Empty("暂无数据"))
                }

                else -> {
                    emit(
                        FoxSdkNetworkResult.PageError(
                            error = response.message ?: "请求失败",
                            page = pageRequest.page,
                            code = response.code
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(handlePageException(e, pageRequest.page))
        }
    }

    /**
     * 执行带缓存的分页网络请求
     */
    fun <T> executePageWithCache(
        pageRequest: FoxSdkPageRequest,
        cacheKey: String,
        cacheProvider: suspend (String, Int, Int) -> List<T>?,
        networkCall: suspend (Int, Int) -> FoxSdkBaseResponse<List<T>>,
        cacheUpdater: suspend (String, Int, List<T>) -> Unit
    ): Flow<FoxSdkNetworkResult<List<T>>> = flow {
        emit(
            FoxSdkNetworkResult.PageLoading(
                isInitial = pageRequest.isInitial,
                page = pageRequest.page
            )
        )

        // 1. 尝试从缓存获取
        if (!pageRequest.isRefresh) {
            val cachedData = cacheProvider(cacheKey, pageRequest.page, pageRequest.pageSize)
            if (cachedData != null && cachedData.isNotEmpty()) {
                val hasMore = cachedData.size >= pageRequest.pageSize
                emit(
                    FoxSdkNetworkResult.PageSuccess(
                        data = cachedData,
                        page = pageRequest.page,
                        hasMore = hasMore
                    )
                )
                return@flow
            }
        }

        // 2. 从网络获取
        try {
            val response = networkCall(pageRequest.page, pageRequest.pageSize)

            when {
                response.isSuccess && response.data != null -> {
                    val data = response.data
                    val hasMore = data.size >= pageRequest.pageSize

                    // 3. 更新缓存
                    try {
                        cacheUpdater(cacheKey, pageRequest.page, data)
                    } catch (e: Exception) {
                        Log.w(TAG, "更新缓存失败", e)
                    }

                    // 4. 发射成功状态
                    if (data.isEmpty() && pageRequest.page == 1) {
                        emit(FoxSdkNetworkResult.Empty("暂无数据"))
                    } else {
                        emit(
                            FoxSdkNetworkResult.PageSuccess(
                                data = data,
                                page = pageRequest.page,
                                hasMore = hasMore
                            )
                        )
                    }
                }

                response.isSuccess && response.data == null -> {
                    emit(FoxSdkNetworkResult.Empty("暂无数据"))
                }

                else -> {
                    emit(
                        FoxSdkNetworkResult.PageError(
                            error = response.message ?: "请求失败",
                            page = pageRequest.page,
                            code = response.code
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(handlePageException(e, pageRequest.page))
        }
    }

    /**
     * 执行带缓存的普通网络请求
     */
    suspend fun <T> executeWithCache(
        cacheKey: String,
        shouldRefresh: Boolean = false,
        cacheProvider: suspend (String) -> T?,
        networkCall: suspend () -> FoxSdkBaseResponse<T>,
        cacheUpdater: suspend (String, T) -> Unit
    ): FoxSdkNetworkResult<T> {
        // 1. 尝试从缓存获取
        if (!shouldRefresh) {
            cacheProvider(cacheKey)?.let { cachedData ->
                return FoxSdkNetworkResult.Success(cachedData, "来自缓存")
            }
        }

        // 2. 从网络获取
        val networkResult = execute(networkCall, showLoading = true)

        return when (networkResult) {
            is FoxSdkNetworkResult.Success -> {
                // 3. 更新缓存
                try {
                    cacheUpdater(cacheKey, networkResult.data)
                } catch (e: Exception) {
                    Log.w(TAG, "更新缓存失败", e)
                }
                networkResult
            }

            is FoxSdkNetworkResult.Error -> {
                // 4. 网络失败时尝试使用缓存
                if (!shouldRefresh) {
                    cacheProvider(cacheKey)?.let { cachedData ->
                        return FoxSdkNetworkResult.Success(cachedData, "网络失败，使用缓存数据")
                    }
                }
                networkResult
            }

            else -> networkResult
        }
    }

    /**
     * 异常处理
     */
    private fun <T> handleException(e: Exception): FoxSdkNetworkResult<T> {
        return when (e) {
            is IOException -> FoxSdkNetworkResult.Error(
                error = "网络连接失败，请检查网络设置",
                code = -1,
                throwable = e
            )

            is HttpException -> FoxSdkNetworkResult.Error(
                error = "网络连接失败，请检查网络设置",
                code = -1,
                throwable = e
            )

            is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> FoxSdkNetworkResult.Error(
                error = "解析错误，请稍后再试",
                code = -1,
                throwable = e
            )

            is ConnectException -> FoxSdkNetworkResult.Error(
                error = "网络连接失败，请检查网络设置",
                code = -1,
                throwable = e
            )

            is javax.net.ssl.SSLException -> FoxSdkNetworkResult.Error(
                error = "证书出错，请稍后再试",
                code = -1,
                throwable = e
            )

            is ConnectTimeoutException -> FoxSdkNetworkResult.Error(
                error = "网络连接超时，请稍后重试",
                code = -1,
                throwable = e
            )

            is java.net.SocketTimeoutException -> FoxSdkNetworkResult.Error(
                error = "网络连接超时，请稍后重试",
                code = -1,
                throwable = e
            )

            is java.net.UnknownHostException -> FoxSdkNetworkResult.Error(
                error = "网络连接超时，请稍后重试",
                code = -1,
                throwable = e
            )

            else -> FoxSdkNetworkResult.Error(
                error = "请求失败: ${e.message}",
                code = -2,
                throwable = e
            )
        }
    }

    /**
     * 分页异常处理
     */
    private fun <T> handlePageException(e: Exception, page: Int): FoxSdkNetworkResult<T> {
        val errorMessage = when (e) {
            is IOException -> "网络连接失败"
            is HttpException -> e.message ?: "网络连接失败，请检查网络设置"

            is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> e.message
                ?: "解析错误，请稍后再试"

            is ConnectException -> e.message ?: "网络连接失败，请检查网络设置"

            is javax.net.ssl.SSLException -> e.message ?: "证书出错，请稍后再试"

            is ConnectTimeoutException -> e.message ?: "网络连接超时，请稍后重试"

            is java.net.SocketTimeoutException -> e.message ?: "网络连接超时，请稍后重试"

            is java.net.UnknownHostException -> e.message ?: "网络连接超时，请稍后重试"

            else -> "加载失败: ${e.message}"
        }
        return FoxSdkNetworkResult.PageError(
            error = errorMessage,
            page = page,
            code = -1
        )
    }
}