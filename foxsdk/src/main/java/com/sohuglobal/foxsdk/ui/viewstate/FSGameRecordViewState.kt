package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSGameRecord

/**
 * @file FSGameRecordViewState
 * 文件说明：游戏记录列表状态
 *
 * @author 王金强
 * @date 2025/10/17 9:41
 */
data class FSGameRecordViewState (
    val gameRecordList: List<FSGameRecord> = emptyList(),
    val moreGameRecordList: List<FSGameRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val totalCount: Int? = null,
    var isInit: Boolean = true
): FoxSdkViewState