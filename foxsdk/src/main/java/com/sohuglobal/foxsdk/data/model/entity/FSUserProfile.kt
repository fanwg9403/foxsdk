package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sohuglobal.foxsdk.core.FoxSdkSPKeys
import com.sohuglobal.foxsdk.utils.FoxSdkSPUtils

/**
 * @Author FHL
 * @CreateTime 2025年 10月 15日 15点 08 分
 * @Desc TODO:
 */
data class FSUserProfile(
    @SerializedName("app_id")
    val appId: Long,

    @SerializedName("channel_id")
    val channelId: Long,

    @SerializedName("game_name")
    val gameName: String,

    @SerializedName("game_url")
    val gameUrl: String,

    val iden: String,

    @SerializedName("open_id")
    val openId: String,

    val phone: String,
    val sign: String,

    @SerializedName("time_stamp")
    val timeStamp: Long,

    val token: String,

    @SerializedName("user_info")
    val userInfo: FSUserInfo
) {
    companion object {
        fun getInstance(): FSUserProfile? =
            Gson().fromJson(
                FoxSdkSPUtils.instance.get(FoxSdkSPKeys.USER_PROFILE),
                FSUserProfile::class.java
            )


        fun save(loginResult: FSUserProfile) {
            FoxSdkSPUtils.instance.put(FoxSdkSPKeys.USER_PROFILE, Gson().toJson(loginResult))
        }

        fun clear() {
            FoxSdkSPUtils.instance.remove(FoxSdkSPKeys.USER_PROFILE)
        }
    }
}