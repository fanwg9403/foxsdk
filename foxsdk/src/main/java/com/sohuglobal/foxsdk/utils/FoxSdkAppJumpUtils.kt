package com.sohuglobal.foxsdk.utils

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import com.hjq.toast.Toaster

/**
 * @file AppJumpUtils
 * 文件说明：跳转app工具
 *
 * @author 王金强
 * @date 2025/10/16 16:12
 */
object FoxSdkAppJumpUtils {




    /**
     * 通过Deep Link跳转
     */
    fun launchByDeepLink(context: Context, deepLink: String,h5Url: String): Boolean {
        return try {
            // 方式2：使用自定义 scheme
            // val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://detail?id=123"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            byWebOpenH5(context,h5Url)
            Log.e("AppJump", "Error launching by deep link: ${e.message}")
            false
        }
    }

    /**
     * 浏览器打开h5页
     * @param context 上下文
     * @param url 网址
     */
    fun byWebOpenH5(context: Context,url: String?){
        url?.let {
            if(url.startsWith("http://")||url.startsWith("https://")){
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            }else{
                Toaster.show("网址无法跳转")
            }
        }?: run {
            Toaster.show("网址无法跳转")
        }
    }
}