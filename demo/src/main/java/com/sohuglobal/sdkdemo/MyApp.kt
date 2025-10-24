package com.sohuglobal.sdkdemo

import android.app.Application
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.core.WishFoxSdk

/**
 *
 * 主要功能:
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 17:42
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        WishFoxSdk.initialize(
            this,
            FoxSdkConfig(
                appId = "1",
                channelId = "1",
                enableLog = true,
                screenOrientation = FoxSdkConfig.ORIENTATION_LANDSCAPE
            )
        )
    }
}