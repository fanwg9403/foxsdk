package com.sohuglobal.foxsdk.utils

import android.content.Context
import com.sohuglobal.foxsdk.core.WishFoxSdk

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 05 分
 * @Desc TODO:
 */
fun Context.dp2px(dp: Int): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Int.dp2px() : Int {
    return (this * WishFoxSdk.getContext().resources.displayMetrics.density + 0.5f).toInt()
}