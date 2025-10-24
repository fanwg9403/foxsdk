package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.Gson
import com.sohuglobal.foxsdk.core.FoxSdkSPKeys
import com.sohuglobal.foxsdk.utils.FoxSdkSPUtils

/**
 * @Author FHL
 * @CreateTime 2025年 10月 21日 14点 55 分
 * @Desc TODO:
 */
data class FSCoinInfo(
    var balanceCoin : String?,
    var rmbToFoxCoinRatio : String?,
) {
    companion object {
        fun getInstance(): FSCoinInfo? =
            Gson().fromJson(
                FoxSdkSPUtils.instance.get(FoxSdkSPKeys.COIN_INFO),
                FSCoinInfo::class.java
            )


        fun save(coinInfo: FSCoinInfo) {
            FoxSdkSPUtils.instance.put(FoxSdkSPKeys.COIN_INFO, Gson().toJson(coinInfo))
        }

        fun clear() {
            FoxSdkSPUtils.instance.remove(FoxSdkSPKeys.COIN_INFO)
        }

    }

    val foxCoin:Int
        get() = (balanceCoin?.toFloat())?.toInt() ?: 0
}
