package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSRechargeRecord

/**
 * @file FSRechargeRecordViewState
 * 文件说明：充值记录列表状态
 *
 * @author 王金强
 * @date 2025/10/16 18:25
 */
data class FSRechargeRecordViewState(
    val rechargeRecordList: List<FSRechargeRecord> = emptyList(),
    val moreRechargeRecordList: List<FSRechargeRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val totalCount: Int? = null,
    var isInit: Boolean = true,
) : FoxSdkViewState
