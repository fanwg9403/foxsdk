package com.sohuglobal.foxsdk.ui.viewmodel

import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.model.paging.PageConstants
import com.sohuglobal.foxsdk.data.repository.FSWinFoxCoinRepository
import com.sohuglobal.foxsdk.domain.intent.FSWinFoxCoinIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSWinFoxCoinViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect

/**
 * @file FSWinFoxCoinViewModel
 * 文件说明：
 *
 * @author 王金强
 * @date 2025/10/14 15:14
 */
class FSWinFoxCoinViewModel(private val winFoxCoinReposity: FSWinFoxCoinRepository) :
    FoxSdkBaseMviViewModel<FSWinFoxCoinViewState, FSWinFoxCoinIntent, FoxSdkUiEffect>() {

    private var currentPage = 1
    private var hasMoreData = true

    override fun initialState(): FSWinFoxCoinViewState = FSWinFoxCoinViewState()

    override suspend fun handleIntent(intent: FSWinFoxCoinIntent) {
        when (intent) {
            is FSWinFoxCoinIntent.LoadInitial -> loadWinFoxCoins()
            is FSWinFoxCoinIntent.Refresh -> loadWinFoxCoins()
            is FSWinFoxCoinIntent.LoadMore -> loadMoreStarterPack()
        }
    }

    //初次请求or刷新
    private fun loadWinFoxCoins(isInitial: Boolean = false) {
        currentPage = 1
        hasMoreData = true

        executePageRequest(
            pageRequest = FoxSdkPageRequest(
                page = currentPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE,
                isInitial = isInitial
            ),
            request = { page, size ->
                winFoxCoinReposity.getWinFoxCoinList(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        winFoxCoinList = data,
                        isLoading = false,
                        isRefreshing = false,
                        isInit = false,
                        error = null,
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
                        isInit = false,
                        isRefreshing = false,
                        error = error
                    )
                }
                //发送错误信息
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },
            onLoading = { isInitial, page ->
                if (isInitial) {
                    updateState {
                        copy(isLoading = true, isInit = false,error = null)
                    }
                } else {
                    updateState { copy(isLoading = true, isInit = false,isRefreshing = true, error = null) }
                }
            }
        )
    }

    //下一页
    private fun loadMoreStarterPack() {
        if (!hasMoreData || viewState.value.isLoadingMore) {
            return
        }
        val nextPage = currentPage + 1
        executePageRequest(
            pageRequest = FoxSdkPageRequest(
                page = nextPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE
            ),
            request = { page, size ->
                winFoxCoinReposity.getWinFoxCoinList(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        moreWinFoxCoinList = data,
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