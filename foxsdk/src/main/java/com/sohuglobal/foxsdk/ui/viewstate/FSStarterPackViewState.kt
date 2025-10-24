package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack

/**
 * @file FSStarterPackViewState
 * 文件说明：新手礼包列表状态
 *
 * @author 王金强
 * @date 2025/10/13 15:33
 */
data class FSStarterPackViewState(
    val starterPackList: List<FSStarterPack> = emptyList(),
    val moreStarterPackList: List<FSStarterPack> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val totalCount: Int? = null,
    val isStateViewEnable: Boolean = false,
    val isInit: Boolean = true
) : FoxSdkViewState
