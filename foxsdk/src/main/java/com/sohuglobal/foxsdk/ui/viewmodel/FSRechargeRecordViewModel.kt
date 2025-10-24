package com.sohuglobal.foxsdk.ui.viewmodel

import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.model.paging.PageConstants
import com.sohuglobal.foxsdk.data.repository.FSRechargeRecordRepository
import com.sohuglobal.foxsdk.domain.intent.FSRechargeRecordIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSRechargeRecordViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect.ShowToast

/**
 * @file FSRechargeRecordViewModel
 * 文件说明：充值记录列表ViewModel
 *
 * @author 王金强
 * @date 2025/10/16 18:33
 */
class FSRechargeRecordViewModel (private val repository: FSRechargeRecordRepository) :
    FoxSdkBaseMviViewModel<FSRechargeRecordViewState, FSRechargeRecordIntent, FoxSdkUiEffect>() {

    private var currentPage = 1
    private var hasMoreData = true

    override fun initialState() = FSRechargeRecordViewState()

    override suspend fun handleIntent(intent: FSRechargeRecordIntent) {
        when (intent) {
            is FSRechargeRecordIntent.LoadInitial -> loadStarterPacks(false)
            is FSRechargeRecordIntent.Refresh -> loadStarterPacks()
            is FSRechargeRecordIntent.LoadMore -> loadMoreStarterPack()
        }
    }

    //初次请求or刷新
    private suspend fun loadStarterPacks(isInitial: Boolean = false) {
        currentPage = 1
        hasMoreData = true

        executePageRequestData(
            pageRequest = FoxSdkPageRequest(
                page = currentPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE,
                isInitial = isInitial
            ),
            request = { page, size ->
                repository.getRechargeRecords(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        rechargeRecordList = data.data ?: arrayListOf(),
                        isLoading = false,
                        isRefreshing = false,
                        error = null,
                        hasMore = hasMore,
                        isInit = false,
                        totalCount = totalCount
                    )
                }
                currentPage = page
                hasMoreData = hasMore
            },
            onError = { error, page, code ->
                updateState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        isInit = false,
                        error = error
                    )
                }
                //发送错误信息
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },
            onLoading = { isInitial, page ->
                if (isInitial) {
                    updateState {
                        copy(isLoading = true, error = null,isInit = false)
                    }
                } else {
                    updateState { copy(isLoading = true,isInit = false, isRefreshing = true, error = null) }
                }
            }
        )
    }

    //下一页
    private suspend fun loadMoreStarterPack() {
        if (!hasMoreData || viewState.value.isLoadingMore) {
            return
        }
        val nextPage = currentPage + 1
        executePageRequestData(
            pageRequest = FoxSdkPageRequest(
                page = nextPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE
            ),
            request = { page, size ->
                repository.getRechargeRecords(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        moreRechargeRecordList = data.data ?: arrayListOf(),
                        isLoadingMore = false,
                        error = null,
                        isInit = false,
                        hasMore = hasMore
                    )
                }
            },
            onError = { error, page, code ->
                updateState {
                    copy(
                        isLoadingMore = false,
                        isInit = false,
                        error = error
                    )
                }
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },
            onLoading = { isInitial, page ->
                updateState { copy(isLoadingMore = true,isInit = false) }
            }
        )
    }

}