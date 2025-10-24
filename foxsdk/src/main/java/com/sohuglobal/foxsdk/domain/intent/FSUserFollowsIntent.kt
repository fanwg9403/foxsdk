package com.sohuglobal.foxsdk.domain.intent

/**
 *
 * 主要功能: 用户关注列表意图
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 15:52
 */
sealed class FSUserFollowsIntent : FoxSdkViewIntent {
    data class LoadInitial(val userId: String) : FSUserFollowsIntent()
    data class Refresh(val userId: String) : FSUserFollowsIntent()
    data class LoadMore(val userId: String) : FSUserFollowsIntent()
}