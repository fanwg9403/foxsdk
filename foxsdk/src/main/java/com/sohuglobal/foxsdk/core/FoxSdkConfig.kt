package com.sohuglobal.foxsdk.core

import android.content.pm.ActivityInfo

/**
 *
 * 主要功能: SDK配置类
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 16:31
 */
data class FoxSdkConfig(
    // 游戏id
    val appId: String,
    // 游戏key
    val channelId: String,
    // 接口域名
    val baseUrl: String = "https://api-game.wishfoxs.com",
    // 是否开启log日志
    val enableLog: Boolean = false,
    // 网络请求超时时间
    val timeout: Long = 30000L,
    // 屏幕方向
    val screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
) {
    companion object {
//        const val ORIENTATION_AUTO = 0
        const val ORIENTATION_PORTRAIT = 1
        const val ORIENTATION_LANDSCAPE = 2

        const val WISH_FOX_REQUEST_CODE = 0x0278887

        const val WISH_FOX_PACKAGE_NAME = "com.sohuglobal.world"

        const val WISH_FOX_AUTH_LOGIN_ACTIVITY = "com.sohuglobal.world.auth.AuthLoginActivity"
    }

    object WishFoxActions {
        const val WISH_FOX_AUTH_ACTION = "com.sohuglobal.world.AUTH_LOGIN"
        const val WISH_FOX_AUTH_RESULT_ACTION = "com.sohuglobal.world.AUTH_LOGIN_RESULT"
    }
}