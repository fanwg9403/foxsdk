package com.sohuglobal.foxsdk.domain.intent

/**
 * @file FSRechargeRecordIntent
 * 文件说明：充值记录列表意图
 *
 * @author 王金强
 * @date 2025/10/16 18:27
 */
sealed class FSRechargeRecordIntent : FoxSdkViewIntent{
    class LoadInitial() : FSRechargeRecordIntent()
    class Refresh() : FSRechargeRecordIntent()
    class LoadMore() : FSRechargeRecordIntent()
}