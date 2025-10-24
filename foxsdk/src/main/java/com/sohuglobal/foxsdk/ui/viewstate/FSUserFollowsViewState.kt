package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo

/**
 *
 * 主要功能: 用户关注列表状态
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 12:12
 */
data class FSUserFollowsViewState(
    val users: List<FSUserInfo> = emptyList(),
    val moreUsers: List<FSUserInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val totalCount: Int? = null
) : FoxSdkViewState