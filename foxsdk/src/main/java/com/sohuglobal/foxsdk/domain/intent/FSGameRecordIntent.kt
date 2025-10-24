package com.sohuglobal.foxsdk.domain.intent

/**
 * @file FSGameRecordIntent
 * 文件说明：游戏记录列表意图
 *
 * @author 王金强
 * @date 2025/10/17 9:47
 */
sealed class FSGameRecordIntent: FoxSdkViewIntent {
    class LoadInitial() : FSGameRecordIntent()
    class Refresh() : FSGameRecordIntent()
    class LoadMore() : FSGameRecordIntent()
}