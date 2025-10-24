package com.sohuglobal.foxsdk.domain.intent

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 40 分
 * @Desc TODO:
 */
sealed class FSHomeIntent : FoxSdkViewIntent {

    data class Login(val phone: String, val code: String, val type: Int) : FSHomeIntent()
    class Logout : FSHomeIntent()
    class Init : FSHomeIntent()
}