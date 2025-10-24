package com.sohuglobal.foxsdk.data.model.network

/**
 *
 * 主要功能: 统一的网络请求结果封装
 * @Description: 用于所有网络请求的返回结果，避免重复定义
 * @author: 范为广
 * @date: 2025年10月12日 10:24
 */
sealed class FoxSdkNetworkResult<out T> {
    /**
     * 空闲状态 - 初始状态
     */
    object Idle : FoxSdkNetworkResult<Nothing>()

    /**
     * 加载中状态
     * @param isRefreshing 是否为刷新操作
     * @param message 加载提示信息
     */
    data class Loading(
        val isRefreshing: Boolean = false,
        val message: String? = null
    ) : FoxSdkNetworkResult<Nothing>()

    /**
     * 成功状态
     * @param data 成功返回的数据
     * @param message 成功提示信息
     */
    data class Success<T>(
        val data: T,
        val message: String? = null
    ) : FoxSdkNetworkResult<T>()

    /**
     * 错误状态
     * @param error 错误信息
     * @param code 错误码
     * @param throwable 异常信息
     */
    data class Error(
        val error: String,
        val code: Int = -1,
        val throwable: Throwable? = null
    ) : FoxSdkNetworkResult<Nothing>()

    /**
     * 空数据状态
     * @param message 空数据提示信息
     */
    data class Empty(
        val message: String = "暂无数据"
    ) : FoxSdkNetworkResult<Nothing>()

    /**
     * 分页加载中状态
     * @param isInitial 是否为初始加载
     * @param page 当前页码
     */
    data class PageLoading(
        val isInitial: Boolean = true,
        val page: Int = 1
    ) : FoxSdkNetworkResult<Nothing>()

    /**
     * 分页成功状态
     * @param data 当前页数据
     * @param page 当前页码
     * @param hasMore 是否还有更多数据
     * @param totalCount 总数据量
     */
    data class PageSuccess<T>(
        val data: T,
        val page: Int,
        val hasMore: Boolean,
        val totalCount: Int? = null
    ) : FoxSdkNetworkResult<T>()

    /**
     * 分页错误状态
     * @param error 错误信息
     * @param page 发生错误的页码
     * @param code 错误码
     */
    data class PageError(
        val error: String,
        val page: Int,
        val code: Int = -1
    ) : FoxSdkNetworkResult<Nothing>()

    // 状态检查扩展属性
    val isIdle: Boolean get() = this is Idle
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success<*>
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty
    val isPageLoading: Boolean get() = this is PageLoading
    val isPageSuccess: Boolean get() = this is PageSuccess<*>
    val isPageError: Boolean get() = this is PageError

    // 数据获取扩展函数
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is PageSuccess -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is PageSuccess -> data
        else -> throw NoSuchElementException("FoxSdkNetworkResult is not Success")
    }

    fun getErrorOrNull(): Error? = this as? Error
    fun getPagedErrorOrNull(): PageError? = this as? PageError

    // 页码获取
    val currentPage: Int?
        get() = when (this) {
            is PageLoading -> page
            is PageSuccess -> page
            is PageError -> page
            else -> null
        }

    // 是否有更多数据
    val hasMoreData: Boolean
        get() = (this as? PageSuccess<*>)?.hasMore ?: false
}