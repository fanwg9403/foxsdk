package com.sohuglobal.foxsdk.data.model.entity

/**
 *
 * 主要功能: 用户实例模型
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 18:35
 */
data class FSUserInfo(
    val userId: String,
    val userName: String? = null,
    val avatar: String? = null,
    val mobile: String? = null,
    var foxCoin: Int? = null,
) {
    companion object {
        fun getInstance(): FSUserInfo? = FSUserProfile.getInstance()?.userInfo
    }
}