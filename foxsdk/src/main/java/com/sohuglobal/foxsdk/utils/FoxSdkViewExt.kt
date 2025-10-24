package com.sohuglobal.foxsdk.utils

import android.view.View

/**
 * @Author FHL
 * @CreateTime 2025年 10月 13日 16点 29 分
 * @Desc 视图扩展方法
 */

/**
 * 点击事件节流
 */
fun View.onClick(block: (View) -> Unit) {
    setOnClickListener { it ->
        FoxSdkUtils.throttle { block.invoke(it) }
    }
}