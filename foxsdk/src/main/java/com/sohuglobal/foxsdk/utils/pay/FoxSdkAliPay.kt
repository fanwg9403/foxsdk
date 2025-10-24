package com.sohuglobal.foxsdk.utils.pay

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R

/**
 * @file FoxSdkAliPay
 * 文件说明：支付包支付
 *
 * @author 王金强
 * @date 2025/10/21 14:31
 */
object FoxSdkAliPay {

    //支付宝yi-ma 支付
    fun payAliYiMa(context: Context,codeUrl: String,jumpUrl:String): Boolean {
        val installed = isAlipayAvailable(context)
        if (!installed) {
            Toaster.show(context.getString(R.string.fs_str_ali_pay))
            return false
        } else {
            if(jumpUrl.isNullOrEmpty()){
                // 固定前缀
                val topic =
                    "alipays://platformapi/startapp?saId=10000007&qrcode="
                // 字符串拼接
                val jumpUrls = "$topic$codeUrl"
                context.startActivity(
                    Intent()
                        .setAction("android.intent.action.VIEW")
                        .setData(jumpUrls.toUri())
                )
            }else{
                context.startActivity(
                    Intent()
                        .setAction("android.intent.action.VIEW")
                        .setData(jumpUrl.toUri())
                )
            }

            return true
        }
    }

    /**
     * 判断支付宝是否可用（安装且版本达标）
     */
    fun isAlipayAvailable(context: Context): Boolean {
        return checkAlipayInstallation(context)
    }

    private fun checkAlipayInstallation(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo("com.eg.android.AlipayGphone", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}