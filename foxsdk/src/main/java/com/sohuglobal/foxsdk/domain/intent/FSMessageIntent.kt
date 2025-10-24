package com.sohuglobal.foxsdk.domain.intent

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 09点 18 分
 * @Desc TODO:
 */
sealed class FSMessageIntent : FoxSdkViewIntent {
    class Refresh : FSMessageIntent()
    data class Read(val id: Long? = null) : FSMessageIntent()
}