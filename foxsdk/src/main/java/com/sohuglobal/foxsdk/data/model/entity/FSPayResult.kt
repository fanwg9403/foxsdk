package com.sohuglobal.foxsdk.data.model.entity

import com.sohuglobal.foxsdk.utils.FoxSdkPayEnum

/**
 * @file FSPayResult
 * 文件说明：支付结果实体
 *
 * @author 王金强
 * @date 2025/10/21 18:10
 */
data class FSPayResult(var isCheckPay: Boolean, var orderId: String,var payType: FoxSdkPayEnum)
