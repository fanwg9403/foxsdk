package com.sohuglobal.foxsdk.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.sohuglobal.foxsdk.core.FoxSdkConfig.Companion.WISH_FOX_PACKAGE_NAME


/**
 *
 * 主要功能: 许愿狐SDK工具类
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 11:33
 */
object FoxSdkUtils {

    const val BUGLY_APPID = "0ddd1ab1b4"

    /**
     * 获取测试信息
     */
    fun getTestMsg(): String {
        return "欢迎使用许愿狐SDK！"
    }

    /**
     * 节流
     */
    private var _lastTime = 0L
    fun throttle(delay: Long = 500, func: () -> Unit) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - _lastTime >= delay) {
            func()
            _lastTime = nowTime
        }
    }

    fun runOnUIThread(block: () -> Unit) {
        Handler(Looper.getMainLooper()).post { block() }
    }

    fun runOnUIThreadDelay(delay: Long, block: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(block, delay)
    }

    /**
     * 将文本复制到剪贴板
     *
     * @param text 要复制的文本
     */
    fun copyText(context: Context, text: String?) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    /**
     * 检查应用是否已安装
     */
    fun isWishFoxInstalled(ctx:Context): Boolean {
        try {
            val pm: PackageManager = ctx.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(WISH_FOX_PACKAGE_NAME)
//            pm.getPackageInfo("com.sohuglobal.world", 0)
            return launchIntent != null
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }


    private fun isIntentAvailable(ctx:Context, intent: Intent): Boolean {
        val pm: PackageManager = ctx.packageManager
        val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.isNotEmpty()
    }
}