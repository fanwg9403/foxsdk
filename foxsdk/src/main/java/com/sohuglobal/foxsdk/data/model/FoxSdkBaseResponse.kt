package com.sohuglobal.foxsdk.data.model

/**
 *
 * 主要功能: API响应基础数据结构
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 14:29
 */
data class FoxSdkBaseResponse<T>(
    val code: Int,
    val msg: String? = null,
    val data: T? = null,
    val total: Int = 0
) {
    val isSuccess: Boolean
        get() = code == 200 || code == 0
}