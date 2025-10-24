package com.sohuglobal.foxsdk.domain.intent

/**
 * @file FSWinFoxCoinIntent
 * 文件说明：
 *
 * @author 王金强
 * @date 2025/10/14 14:41
 */
sealed class FSWinFoxCoinIntent: FoxSdkViewIntent {
    class LoadInitial() : FSWinFoxCoinIntent()
    class Refresh() : FSWinFoxCoinIntent()
    class LoadMore() : FSWinFoxCoinIntent()
}