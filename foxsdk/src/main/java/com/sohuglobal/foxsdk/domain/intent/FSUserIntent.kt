package com.sohuglobal.foxsdk.domain.intent

/**
 *
 * 主要功能: 用户意图
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 12:15
 */
sealed class FSUserIntent : FoxSdkViewIntent {
    object GetUserDetail : FSUserIntent()
}