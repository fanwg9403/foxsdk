package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSHomeBanner
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 40 分
 * @Desc TODO:
 */
class FSHomeViewState(
    val userInfo: FSUserInfo? = null,
    val bannerList: List<FSHomeBanner> = emptyList(),
    val coinInfo: FSCoinInfo? = null,
    val loginSuccess: Boolean = true,
): FoxSdkViewState