package com.sohuglobal.foxsdk.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import com.hjq.toast.Toaster
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.FxScopeType
import com.petterp.floatingx.listener.IFxTouchListener
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager
import com.sohuglobal.foxsdk.ui.view.activity.FSHomeActivity
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.dp2px
import com.sohuglobal.foxsdk.utils.customerservice.QiyukfHelper
import com.tencent.bugly.crashreport.CrashReport

/**
 *
 * 主要功能: SDK初始化入口
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 17:14
 */
@SuppressLint("StaticFieldLeak")
object WishFoxSdk {

    private const val TAG = "FoxSdk[Init]"
    private var isInitialized = false
    private lateinit var config: FoxSdkConfig
    private lateinit var context: Context

    /**
     * 初始化SDK
     * 需要在Application中进行
     */
    fun initialize(context: Context, config: FoxSdkConfig) {
        if (context !is Application) throw IllegalStateException("WishFoxSdk 初始化需要再Application中进行")

        this.context = context.applicationContext
        this.config = config
        this.isInitialized = true

        // 初始化网络组件
        FoxSdkRetrofitManager.initialize(config)

        // 初始化Toast工具
        Toaster.init(context)

        // 初始化bugly
        CrashReport.initCrashReport(
            context,
            FoxSdkUtils.BUGLY_APPID,
            config.enableLog
        )
        //初始化七鱼客服
        QiyukfHelper.instance.init(context, FoxSdkBaseMviActivity::class.java)
        QiyukfHelper.instance.initKFSDK()

        val fxAppControl = FloatingX.install {
            setOffsetXY(0f, context.dp2px(100).toFloat())
            setContext(context)
            setLayout(R.layout.fs_floating_view)
            setScopeType(FxScopeType.APP)
            setEnableAnimation(true)
            addInstallBlackClass(
                "com.sohuglobal.foxsdk.ui.view.activity.FSHomeActivity",
                "com.sohuglobal.foxsdk.ui.view.activity.FSStarterPackActivity",
                "com.sohuglobal.foxsdk.ui.view.activity.FSWinFoxCoinActivity",
                "com.sohuglobal.foxsdk.ui.view.activity.FSRechargeRecordActivity",
                "com.sohuglobal.foxsdk.ui.view.activity.FSGameRecordActivity",
                "com.qiyukf.unicorn.ui.activity.ServiceMessageActivity",
                "com.sohuglobal.foxsdk.ui.view.activity.FSMessageActivity",
            )
        }
        fxAppControl.setClickListener { it ->
            it?.context?.let { ctx ->
                fxAppControl.getView()?.let { it ->
                    context.startActivity(Intent(ctx, FSHomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        putExtra("isSpecificCondition", true)
                    })
                }
            }
        }
        fxAppControl.show()

        if (config.enableLog)
            Log.d(TAG, "WishFoxSDK 初始化成功！")
    }

    /**
     * 检查SDK是否已初始化
     */
    fun requireInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("必须要先初始化WishFoxSDK. 初始化方法：WishFoxSDK.initialize()")
        }
    }

    /**
     * 获取配置信息
     */
    fun getConfig(): FoxSdkConfig {
        requireInitialized()
        return config
    }

    /**
     * 获取上下文
     */
    fun getContext(): Context {
        requireInitialized()
        return context
    }
}