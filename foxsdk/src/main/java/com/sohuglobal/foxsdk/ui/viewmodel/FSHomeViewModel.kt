package com.sohuglobal.foxsdk.ui.viewmodel

import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSHomeBanner
import com.sohuglobal.foxsdk.data.model.entity.FSLoginResult
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.repository.FSHomeRepository
import com.sohuglobal.foxsdk.domain.intent.FSHomeIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.view.dialog.FSLoginDialog
import com.sohuglobal.foxsdk.ui.viewstate.FSHomeViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect
import com.sohuglobal.foxsdk.utils.FoxSdkConstant
import com.sohuglobal.foxsdk.utils.FoxSdkSPUtils

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 33 分
 * @Desc TODO:
 */
class FSHomeViewModel(val repository: FSHomeRepository) :
    FoxSdkBaseMviViewModel<FSHomeViewState, FSHomeIntent, FoxSdkUiEffect>() {
    override fun initialState(): FSHomeViewState {
        return FSHomeViewState()
    }

    override suspend fun handleIntent(intent: FSHomeIntent) {
        when (intent) {
            is FSHomeIntent.Login -> {
                val result = if (intent.type == 1) {
                    repository.loginByPassword(intent.phone, intent.code)
                } else {
                    repository.loginByVerifyCode(intent.phone, intent.code)
                }
                if (result is FoxSdkNetworkResult.Success) {
                    FSLoginResult.save(result.data)
                    getUserInfo()
                } else if (result is FoxSdkNetworkResult.Error) {
                    FSLoginDialog.dismissLoading()
                    Toaster.show(result.error)
                    setState(FSHomeViewState(loginSuccess = false))
                }
            }

            is FSHomeIntent.Init -> {
                val res = repository.getAdvertiseList()
                var ad: List<FSHomeBanner>? = null
                if (res is FoxSdkNetworkResult.Success) {
                    ad = res.data
                }
                var coinInfo: FSCoinInfo? = null
                if (FSUserInfo.getInstance() != null) {
                    val result = repository.getUserVirtualInfo()
                    if (result is FoxSdkNetworkResult.Success) {
                        FSCoinInfo.save(result.data)
                        coinInfo = result.data
                    }
                }
                setState(
                    FSHomeViewState(
                        userInfo = FSUserInfo.getInstance(),
                        coinInfo = coinInfo,
                        bannerList = ad ?: emptyList(),
                    )
                )
            }

            is FSHomeIntent.Logout -> {
                FSUserProfile.clear()
                FSLoginResult.clear()
                FoxSdkSPUtils.instance.remove(FoxSdkConstant.AUTHORIZATION)
                FSCoinInfo.clear()
                repository.logout()
                val res = repository.getAdvertiseList()
                var ad: List<FSHomeBanner>? = null
                if (res is FoxSdkNetworkResult.Success) {
                    ad = res.data
                }
                setState(
                    FSHomeViewState(
                        userInfo = null,
                        bannerList = ad ?: emptyList()
                    )
                )
            }
        }
    }

    suspend fun getUserInfo() {
        val result = repository.getUserInfo()
        if (result is FoxSdkNetworkResult.Success) {
            FSUserProfile.save(result.data)
            val res = repository.getUserVirtualInfo()
            var coinInfo: FSCoinInfo? = null
            if (res is FoxSdkNetworkResult.Success) {
                FSCoinInfo.save(res.data)
                coinInfo = res.data
            }
            val response = repository.getAdvertiseList()
            var ad: List<FSHomeBanner>? = null
            if (response is FoxSdkNetworkResult.Success) {
                ad = response.data
            }
            setState(
                FSHomeViewState(
                    userInfo = result.data.userInfo,
                    coinInfo = coinInfo,
                    bannerList = ad ?: emptyList(),
                )
            )
            FSLoginDialog.dismiss()
            Toaster.show("登录成功")
        } else if (result is FoxSdkNetworkResult.Error) {
            FSLoginDialog.dismissLoading()
            Toaster.show(result.error)
            setState(FSHomeViewState(loginSuccess = false))
        }
    }
}