package com.sohuglobal.foxsdk.data.network.interceptor

import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.data.model.entity.FSLoginResult
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 *
 * 主要功能: 增强的Header拦截器，包含详细的请求头信息
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 16:36
 */
class FoxSdkHeaderInterceptor(private val config: FoxSdkConfig) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // 构建新的请求，添加通用Header
        val requestBuilder = original.newBuilder()
            .header("Lang", if (Locale.getDefault().language == "zh") "zh_CN" else "en_US")
            .header("Content-Type", "multipart/form-data")
            .header("Platform", "android")
            .header("SysSource", "wishfoxSdk")
            .header("Platform-Type", "USER")
            .header("AppId", config.appId)
            .header("ChannelId", config.channelId)
            .header("Authorization", FSLoginResult.getToken())
            .header("Version", "1.5.5")

        val request = requestBuilder.method(original.method, original.body).build()

        return chain.proceed(request)
    }
}