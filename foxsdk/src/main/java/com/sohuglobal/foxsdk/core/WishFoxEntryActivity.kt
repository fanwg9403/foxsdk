package com.sohuglobal.foxsdk.core

import android.app.Activity
import android.app.ComponentCaller
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.net.toUri
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import java.io.ByteArrayOutputStream

/**
 * @Author FHL
 * @CreateTime 2025年 10月 22日 14点 22 分
 * @Desc TODO:
 */
class WishFoxEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fs_wish_fox_entry_activity)
        ImmersionBar.with(this)
//            .fitsSystemWindows(true)
            .transparentBar()
            .init()
        FoxSdkUtils.runOnUIThreadDelay(200) { handlerIntent(intent) }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        handlerIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handlerIntent(it) }
    }

    private fun handlerIntent(intent: Intent) {
        val action = intent.action
        when (action) {
            FoxSdkConfig.WishFoxActions.WISH_FOX_AUTH_ACTION -> {
                FoxSdkUtils.runOnUIThread {
                    val installed = FoxSdkUtils.isWishFoxInstalled(this)
                    if (installed) {
                        val sendIntent = Intent(action)
                        sendIntent.component = ComponentName(
                            FoxSdkConfig.Companion.WISH_FOX_PACKAGE_NAME,
                            FoxSdkConfig.Companion.WISH_FOX_AUTH_LOGIN_ACTIVITY
                        )
                        sendIntent.putExtra("appName", "WishFoxSdkDemo")
                        val bitmap =
                            BitmapFactory.decodeResource(this.resources, R.mipmap.ic_kf_icon)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val byteArray = stream.toByteArray()
                        sendIntent.putExtra("icon", byteArray)
                        sendIntent.putExtra("package", packageName)
                        try {
                            startActivity(sendIntent)
                            bitmap.recycle()
                            finish()
                        } catch (e: ActivityNotFoundException) {
                            // 目标应用未安装则跳转H5下载页
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://world.sohuglobal.com/downloadApp.html".toUri()
                                )
                            )
                            bitmap.recycle()
                            finish()
                        }
                    } else {
                        // 目标应用未安装则跳转H5下载页
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://world.sohuglobal.com/downloadApp.html".toUri()
                            )
                        )
                        finish()
                    }
                }
            }

            FoxSdkConfig.WishFoxActions.WISH_FOX_AUTH_RESULT_ACTION -> {
                val message = intent.getStringExtra("message")
                Toaster.show(message ?: "")
                finish()
            }
        }
    }

}