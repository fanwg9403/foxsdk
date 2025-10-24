package com.sohuglobal.foxsdk.ui.viewmodel

import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.model.paging.PageConstants
import com.sohuglobal.foxsdk.data.repository.FSGameRecordRepository
import com.sohuglobal.foxsdk.domain.intent.FSGameRecordIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSGameRecordViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect

/**
 * @file FSGameRecordViewModel
 * 文件说明：游戏记录列表ViewModel
 *
 * @author 王金强
 * @date 2025/10/17 9:50
 */
class FSGameRecordViewModel(private val gameRecordRepository: FSGameRecordRepository) :
    FoxSdkBaseMviViewModel<FSGameRecordViewState, FSGameRecordIntent, FoxSdkUiEffect>() {

    private var currentPage = 1
    private var hasMoreData = true
    override fun initialState(): FSGameRecordViewState = FSGameRecordViewState()


    override suspend fun handleIntent(intent: FSGameRecordIntent) {
        when (intent) {
            is FSGameRecordIntent.LoadInitial -> loadGameRecords(false)
            is FSGameRecordIntent.Refresh -> loadGameRecords()
            is FSGameRecordIntent.LoadMore -> loadMoreGameRecord()
        }
    }

    //初次请求or刷新
    private fun loadGameRecords(isInitial: Boolean = false) {
        currentPage = 1
        hasMoreData = true

        executePageRequestData(
            pageRequest = FoxSdkPageRequest(
                page = currentPage,
                pageSize = 50,
                isInitial = isInitial
            ),
            request = { page, size ->
                gameRecordRepository.getGameRecordList(
                    page, size, WishFoxSdk.getConfig().channelId,
                    WishFoxSdk.getConfig().appId
                )
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        gameRecordList = data.data ?: emptyList(),
                        isLoading = false,
                        isRefreshing = false,
                        error = null,
                        isInit = false,
                        hasMore = hasMore,
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
                        copy(isLoading = true,isInit = false, error = null)
                    }
                } else {
                    updateState { copy(isLoading = true, isInit = false,isRefreshing = true, error = null) }
                }
            }
        )
    }

    //下一页
    private fun loadMoreGameRecord() {
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
                gameRecordRepository.getGameRecordList(
                    page, size, WishFoxSdk.getConfig().channelId,
                    WishFoxSdk.getConfig().appId
                )
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        moreGameRecordList = data.data ?: emptyList(),
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