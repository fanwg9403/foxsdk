package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile

/**
 *
 * 主要功能: 用户详情状态
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 12:09
 */
data class FSUserViewState(
    val user: FSUserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : FoxSdkViewState