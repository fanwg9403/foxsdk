package com.sohuglobal.foxsdk.ui.viewmodel

import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.repository.FSMessageRepository
import com.sohuglobal.foxsdk.domain.intent.FSMessageIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSMessageViewState
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 09点 18 分
 * @Desc 消息列表 ViewModel
 */
class FSMessageViewModel(val repository: FSMessageRepository) :
    FoxSdkBaseMviViewModel<FSMessageViewState, FSMessageIntent, FoxSdkUiEffect>() {
    override fun initialState() = FSMessageViewState.Init()

    override suspend fun handleIntent(intent: FSMessageIntent) {
        when (intent) {
            is FSMessageIntent.Refresh -> {
                val result = repository.getMessageList(
                    1,
                    100,
                    WishFoxSdk.getConfig().channelId,
                    WishFoxSdk.getConfig().appId
                )
                if (result is FoxSdkNetworkResult.Success) {
                    setState(
                        FSMessageViewState.LoadList(
                            list = result.data.data ?: ArrayList(),
                            isRefresh = true,
                            hasMore = false
                        )
                    )
                } else if (result is FoxSdkNetworkResult.Error) {
                    setState(
                        FSMessageViewState.LoadList(
                            list = ArrayList(),
                            isRefresh = true,
                            hasMore = false
                        )
                    )
                    Toaster.show(result.error)
                }
            }

            is FSMessageIntent.Read -> {
               val result = repository.read(intent.id)
                if (result is FoxSdkNetworkResult.Success) {
                    setState(FSMessageViewState.Read(intent.id))
                } else if (result is FoxSdkNetworkResult.Error) {
                    Toaster.show(result.error)
                }
            }
        }
    }
}