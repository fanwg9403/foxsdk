package com.sohuglobal.foxsdk.ui.viewmodel

import com.sohuglobal.foxsdk.data.model.paging.FoxSdkPageRequest
import com.sohuglobal.foxsdk.data.model.paging.PageConstants
import com.sohuglobal.foxsdk.data.repository.FSUserRepository
import com.sohuglobal.foxsdk.domain.intent.FSUserFollowsIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSUserFollowsViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect

/**
 *
 * 主要功能: 用户关注列表ViewModel
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 14:10
 */
class FSUserFollowsViewModel(
    private val userRepository: FSUserRepository
) : FoxSdkBaseMviViewModel<FSUserFollowsViewState, FSUserFollowsIntent, FoxSdkUiEffect>() {

    private var currentPage = 1
    private var hasMoreData = true

    override fun initialState(): FSUserFollowsViewState = FSUserFollowsViewState()

    override suspend fun handleIntent(intent: FSUserFollowsIntent) {
        when (intent) {
            is FSUserFollowsIntent.LoadInitial -> loadUserFollows(intent.userId, true)
            is FSUserFollowsIntent.Refresh -> loadUserFollows(intent.userId)
            is FSUserFollowsIntent.LoadMore -> loadMoreUsers(intent.userId)
        }
    }

    private fun loadUserFollows(userId: String, isInitial: Boolean = false) {
        currentPage = 1
        hasMoreData = true

        executePageRequest(
            pageRequest = FoxSdkPageRequest(
                page = currentPage,
                pageSize = PageConstants.DEFAULT_PAGE_SIZE,
                isInitial = isInitial
            ),
            request = { page, size ->
                userRepository.getUserFollows(userId, page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        users = data,
                        isLoading = false,
                        isRefreshing = false,
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
                        isRefreshing = false,
                        error = error
                    )
                }
                // 发送错误提示
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },
            onLoading = { isInitial, page ->
                if (isInitial) {
                    updateState { copy(isLoading = true, error = null) }
                } else {
                    updateState { copy(isLoading = true, isRefreshing = true, error = null) }
                }
            }
        )
    }

    private fun loadMoreUsers(userId: String) {
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
                userRepository.getUserFollows(userId, page, size)
            },
            onSuccess = { data, page, hasMore, totalCount ->
                updateState {
                    copy(
                        moreUsers = data,
                        isLoadingMore = false,
                        error = null,
                        hasMore = hasMore
                    )
                }
                currentPage = page
                hasMoreData = hasMore
            },
            onError = { error, page, code ->
                updateState {
                    copy(
                        isLoadingMore = false,
                        error = error
                    )
                }
                sendEffect(FoxSdkUiEffect.ShowToast(error))
            },
            onLoading = { isInitial, page ->
                updateState { copy(isLoadingMore = true) }
            }
        )
    }
}