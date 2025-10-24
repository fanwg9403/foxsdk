package com.sohuglobal.foxsdk.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.network.FoxSdkNetworkExecutor
import com.sohuglobal.foxsdk.domain.intent.FoxSdkViewIntent
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *
 * 主要功能: MVI架构的BaseViewModel
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 9:38
 *
 * @param State 页面状态类型
 * @param Intent 用户意图类型
 * @param Effect UI效果类型
 */
abstract class FoxSdkBaseMviViewModel<State : FoxSdkViewState, Intent : FoxSdkViewIntent, Effect : FoxSdkUiEffect> :
    ViewModel() {

    // 页面状态
    private val _viewState = MutableStateFlow(initialState())
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    // UI效果（一次性事件）
    private val _uiEffect = MutableSharedFlow<Effect>()
    val uiEffect: SharedFlow<Effect> = _uiEffect.asSharedFlow()

    // Loading状态管理
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    /**
     * 初始状态
     */
    protected abstract fun initialState(): State

    /**
     * 处理用户意图
     */
    protected abstract suspend fun handleIntent(intent: Intent)

    /**
     * 发送意图
     */
    fun dispatch(intent: Intent) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    /**
     * 更新状态
     */
    protected fun updateState(update: State.() -> State) {
        _viewState.update { it.update() }
    }

    /**
     * 设置新状态
     */
    protected fun setState(newState: State) {
        _viewState.value = newState
    }

    /**
     * 发送UI效果 - 非挂起
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    /**
     * 发送UI效果 - 挂起
     */
    protected suspend fun sendEffectSuspended(effect: Effect) {
        _uiEffect.emit(effect)
    }

    /**
     * 显示Loading
     */
    protected fun showLoading(message: String? = null) {
        _loadingState.value = LoadingState.Show(message)
    }

    /**
     * 隐藏Loading
     */
    protected fun dismissLoading() {
        _loadingState.value = LoadingState.Dismiss
    }

    /**
     * 执行普通网络请求的通用方法
     */
    protected suspend fun <T> executeNetworkRequest(
        request: suspend () -> FoxSdkBaseResponse<T>,
        showLoading: Boolean = true,
        loadingMessage: String? = null,
        onSuccess: (T) -> Unit = {},
        onError: (String, Int?) -> Unit = { _, _ -> }
    ) {
        if (showLoading)
            showLoading(loadingMessage)

        val result = FoxSdkNetworkExecutor.execute(request, showLoading = false)

        when (result) {
            is FoxSdkNetworkResult.Success -> {
                onSuccess(result.data)
                if (showLoading)
                    dismissLoading()
            }

            is FoxSdkNetworkResult.Error -> {
                onError(result.error, result.code)
                if (showLoading)
                    dismissLoading()
            }

            is FoxSdkNetworkResult.Empty -> {
                // 处理空数据
                if (showLoading)
                    dismissLoading()
            }

            else -> {
                // 其他状态不需要特殊处理
                if (showLoading)
                    dismissLoading()
            }
        }
    }

    /**
     * 执行分页网络请求
     */
    protected fun <T> executePageRequest(
        pageRequest: FoxSdkPageRequest,
        request: suspend (Int, Int) -> FoxSdkBaseResponse<List<T>>,
        onSuccess: (List<T>, Int, Boolean, Int?) -> Unit = { _, _, _, _ -> },
        onError: (String, Int, Int?) -> Unit = { _, _, _ -> },
        onLoading: (Boolean, Int) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            FoxSdkNetworkExecutor.executePage(pageRequest, request).collect { result ->
                when (result) {
                    is FoxSdkNetworkResult.PageLoading -> {
                        onLoading(result.isInitial, result.page)
                        if (result.isInitial)
                            showLoading("加载中...")
                    }

                    is FoxSdkNetworkResult.PageSuccess -> {
                        onSuccess(result.data, result.page, result.hasMore, result.totalCount)
                        if (result.page == 1)
                            dismissLoading()
                    }

                    is FoxSdkNetworkResult.PageError -> {
                        onError(result.error, result.page, result.code)
                        dismissLoading()
                    }

                    else -> {
                        // 处理其他状态
                    }
                }
            }
        }
    }
    /**
     * 执行分页网络请求 返回data
     */
    protected fun <T> executePageRequestData(
        pageRequest: FoxSdkPageRequest,
        request: suspend (Int, Int) -> FoxSdkBaseResponse<T>,
        onSuccess: (T, Int, Boolean, Int?) -> Unit = { _, _, _, _ -> },
        onError: (String, Int, Int?) -> Unit = { _, _, _ -> },
        onLoading: (Boolean, Int) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            FoxSdkNetworkExecutor.executePageData(pageRequest, request).collect { result ->
                var gson = Gson()
                Log.e("result","-------"+gson.toJson(result))
                when (result) {
                    is FoxSdkNetworkResult.PageLoading -> {
                        onLoading(result.isInitial, result.page)
                        if (result.isInitial)
                            showLoading("加载中...")
                    }

                    is FoxSdkNetworkResult.PageSuccess -> {
                        when(result.data){
                            is FSPageContainer<*>->{
                                onSuccess(result.data, result.page,
                                    (result.data.lastPage ?: 0) > (result.data.currentPage ?: 0), result.totalCount)
                            }
                            else -> {
                                onSuccess(result.data, result.page, result.hasMore, result.totalCount)
                            }
                        }

                        if (result.page == 1)
                            dismissLoading()
                    }

                    is FoxSdkNetworkResult.PageError -> {
                        onError(result.error, result.page, result.code)
                        dismissLoading()
                    }

                    else -> {
                        // 处理其他状态
                    }
                }
            }
        }
    }
}

/**
 * Loading状态
 */
sealed class LoadingState {
    object Idle : LoadingState()
    data class Show(val message: String? = null) : LoadingState()
    object Dismiss : LoadingState()
}