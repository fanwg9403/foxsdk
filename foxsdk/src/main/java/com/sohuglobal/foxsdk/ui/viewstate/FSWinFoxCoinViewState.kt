package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.data.model.entity.FSWinFoxCoin

/**
 * @file FSWinFoxCoinViewState
 * 文件说明：赢狐币列表状态
 *
 * @author 王金强
 * @date 2025/10/14 14:37
 */
data class FSWinFoxCoinViewState(
    val winFoxCoinList: List<FSWinFoxCoin> = emptyList(),
    val moreWinFoxCoinList: List<FSWinFoxCoin> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val totalCount: Int? = null,
    val isInit: Boolean = true
): FoxSdkViewState
