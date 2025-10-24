package com.sohuglobal.foxsdk.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

/**
 * @Author FHL
 * @CreateTime 2025年 10月 16日 16点 51 分
 * @Desc 动画扩展
 */

/**
 * 平移动画
 * @param orientation 移动方向 HORIZONTAL, VERTICAL
 * @param start 开始位置 单位dp
 * @param end 结束位置 单位dp
 * @param duration 动画时长 单位毫秒
 * @param callback 动画结束回调
 */
fun View.translation(
    orientation: String,
    start: Int,
    end: Int,
    duration: Long,
    callback: (() -> Unit)? = null
) {
    val animator: ObjectAnimator = ObjectAnimator.ofFloat(
        this,
        if (orientation == "HORIZONTAL") "translationX" else "translationY",
        start.dp2px().toFloat(),
        end.dp2px().toFloat()
    )
    animator.duration = duration
    animator.start()
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            callback?.invoke()
        }
    })
}