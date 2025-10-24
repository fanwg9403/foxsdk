package com.sohuglobal.foxsdk.utils.custom

import android.os.Looper

/**
 * @Author FHL
 * @CreateTime 2025年 10月 13日 17点 27 分
 * @Desc 自定义LiveData, 不依赖livecycle, 但是非主线程使用不会触发回调，同时只有一个 observer
 */
class CustomLiveData<T>(value: T? = null) {
    private var currentValue: T? = value
    private var observer: ((T) -> Unit)? = null

    val value: T? get() = currentValue
    fun observe(listener: (T) -> Unit) {
        observer = listener
    }

    /**
     * 如果值相等不触发监听
     */
    fun setValue(value: T) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            if (currentValue != value) {
                observer?.invoke(value)
            }
        }
    }

    /**
     * 不论值是否相等都触发监听
     */
    fun postValue(value: T) {
        currentValue = value
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            observer?.invoke(value)
        }
    }
}