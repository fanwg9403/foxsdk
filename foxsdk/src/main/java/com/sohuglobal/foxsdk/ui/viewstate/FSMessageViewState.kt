package com.sohuglobal.foxsdk.ui.viewstate

import com.sohuglobal.foxsdk.data.model.entity.FSMessage

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 09点 12 分
 * @Desc 消息列表状态
 */
sealed class FSMessageViewState : FoxSdkViewState {

    class Init : FSMessageViewState()

    data class LoadList(
        var list: MutableList<FSMessage> = ArrayList(),
        val isRefresh: Boolean = false,
        val hasMore: Boolean = false,
        private val timestamp: Long = System.currentTimeMillis(),
    ) : FSMessageViewState()

    data class Read(val id: Long? = null) : FSMessageViewState()
}