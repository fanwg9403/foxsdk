package com.sohuglobal.foxsdk.domain.intent

/**
 * @file FSStarterPackIntent
 * 文件说明：新手礼包列表意图
 *
 * @author 王金强
 * @date 2025/10/13 15:36
 */
sealed class FSStarterPackIntent : FoxSdkViewIntent {
    class LoadInitial() : FSStarterPackIntent()
    class Refresh() : FSStarterPackIntent()
    class LoadMore() : FSStarterPackIntent()
    data class ReceiveStarterPack(val mailId: String) : FSStarterPackIntent()
}