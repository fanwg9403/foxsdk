package com.sohuglobal.foxsdk.ui.viewmodel

import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.model.paging.PageConstants
import com.sohuglobal.foxsdk.data.model.paging.pageRequest
import com.sohuglobal.foxsdk.data.repository.FSStarterPackRepository
import com.sohuglobal.foxsdk.domain.intent.FSStarterPackIntent
import com.sohuglobal.foxsdk.domain.intent.FSUserFollowsIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSStarterPackViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect

/**
 * @file FSStarterPackViewModel
 * 文件说明：新手礼包列表ViewModel
 *
 * @author 王金强
 * @date 2025/10/13 15:49
 */
class FSStarterPackViewModel(private val starterPackReposity: FSStarterPackRepository) :
    FoxSdkBaseMviViewModel<FSStarterPackViewState, FSStarterPackIntent, FoxSdkUiEffect>() {

    private var currentPage = 1
    private var hasMoreData = true

    var clicklPostion = -1

    override fun initialState(): FSStarterPackViewState = FSStarterPackViewState()

    override suspend fun handleIntent(intent: FSStarterPackIntent) {
        when (intent) {
            is FSStarterPackIntent.LoadInitial -> loadStarterPacks()
            is FSStarterPackIntent.Refresh -> loadStarterPacks()
            is FSStarterPackIntent.LoadMore -> loadMoreStarterPack()
            is FSStarterPackIntent.ReceiveStarterPack -> receiveStarterPack(mailId = intent.mailId)
        }
    }

    //初次请求or刷新
    private fun loadStarterPacks(isInitial: Boolean = false) {
        currentPage = 1
        hasMoreData = true

        executePageRequestData(
            pageRequest = FoxSdkPageRequest(
                page = currentPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE,
                isInitial = isInitial
            ),
            request = { page, size ->
                starterPackReposity.getStarterPackList(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        starterPackList = data.data ?: emptyList(),
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
    private fun loadMoreStarterPack() {
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
                starterPackReposity.getStarterPackList(page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        moreStarterPackList = data.data ?: emptyList(),
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


    //领取新手礼包
    suspend fun receiveStarterPack(mailId: String) {
        executeNetworkRequest(
            request = {
                starterPackReposity.receiveStarterPack(mailId)
            },
            onSuccess = {
                updateState {
                    copy(
                        isStateViewEnable = true
                    )
                }
            },
            onError = { error, code ->
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },

        )
    }

    //修改领取状态
    fun modifyReceiveState() {
        updateState {
            copy(
                isStateViewEnable = false
            )
        }
    }

}
