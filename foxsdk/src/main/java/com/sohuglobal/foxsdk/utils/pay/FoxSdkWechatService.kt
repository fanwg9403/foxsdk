package com.sohuglobal.foxsdk.utils.pay

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object FoxSdkWechatService {
    private const val TIMELINE_SUPPORTED_VERSION = 0x21020001

    @JvmStatic
    var iwxapi: IWXAPI? = null
        private set

    /**
     * 初始化微信 SDK
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun init(context: Context) {
        iwxapi = WXAPIFactory.createWXAPI(context, FoxSdkPayConfig.APP_ID, true)
        iwxapi!!.registerApp(FoxSdkPayConfig.APP_ID)
        val filter = IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP)
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                iwxapi!!.registerApp(FoxSdkPayConfig.APP_ID)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
    }

    val isWeChatInstalled: Boolean
        /**
         * 检查是否安装了微信客户端
         */
        get() {
            if (iwxapi != null) {
                return iwxapi!!.isWXAppInstalled()
            }
            return false
        }

    val isWeChatTimelineSupported: Boolean
        /**
         * 检查微信客户端是否支持分享到朋友圈
         */
        get() {
            if (iwxapi != null) {
                return iwxapi!!.getWXAppSupportAPI() >= TIMELINE_SUPPORTED_VERSION
            }
            return false
        }
}
