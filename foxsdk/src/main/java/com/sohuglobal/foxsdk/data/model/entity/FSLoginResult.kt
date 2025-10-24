package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sohuglobal.foxsdk.core.FoxSdkSPKeys
import com.sohuglobal.foxsdk.utils.FoxSdkSPUtils

/**
 * @Author FHL
 * @CreateTime 2025年 10月 15日 09点 33 分
 * @Desc 登录返回结果
 */
data class FSLoginResult(
    @SerializedName("app_id")
    val appId: Long?,
    @SerializedName("channel_id")
    val channelId: Long?,
    @SerializedName("game_name")
    val gameName: String?,
    @SerializedName("game_url")
    val gameUrl: String,
    @SerializedName("open_id")
    val openId: String?,
    val phone: String?,
    val sign: String?,
    @SerializedName("time_stamp")
    val timeStamp: Long?,
    val token: String?
) {
    companion object {
        fun getInstance(): FSLoginResult? =
            Gson().fromJson(
                FoxSdkSPUtils.instance.get(FoxSdkSPKeys.LOGIN_RESULT),
                FSLoginResult::class.java
            )


        fun save(loginResult: FSLoginResult) {
            FoxSdkSPUtils.instance.put(FoxSdkSPKeys.LOGIN_RESULT, Gson().toJson(loginResult))
        }

        fun getToken(): String {
            return getInstance()?.token ?: ""
        }

        fun clear() {
            FoxSdkSPUtils.instance.remove(FoxSdkSPKeys.LOGIN_RESULT)
        }
    }
}
