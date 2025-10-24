package com.sohuglobal.foxsdk.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.data.network.interceptor.FoxSdkHeaderInterceptor
import com.sohuglobal.foxsdk.data.network.interceptor.FoxSdkLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 * 主要功能: Retrofit配置管理
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 16:26
 */
object FoxSdkRetrofitManager {

    private const val TAG = "FoxSdk[RetrofitManager]"
    private var retrofit: Retrofit? = null
    private var apiService: FoxSdkApiService? = null

    fun initialize(config: FoxSdkConfig) {
        val client = OkHttpClient.Builder()
            .connectTimeout(config.timeout, TimeUnit.MILLISECONDS)
            .readTimeout(config.timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(config.timeout, TimeUnit.MILLISECONDS)
            .addInterceptor(FoxSdkHeaderInterceptor(config))
            .addInterceptor(FoxSdkLoggingInterceptor(config))
            .apply {
                // 不再使用HttpLoggingInterceptor，已有详细的日志拦截器
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()

        apiService = retrofit!!.create(FoxSdkApiService::class.java)

        if (config.enableLog)
            Log.d(TAG, "RetrofitManager 初始化完成")
    }

    fun getApiService(): FoxSdkApiService {
        return apiService ?: throw IllegalStateException("RetrofitManager 没有初始化")
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }
}