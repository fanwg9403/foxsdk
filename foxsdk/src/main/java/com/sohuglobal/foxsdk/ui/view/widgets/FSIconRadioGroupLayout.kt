package com.sohuglobal.foxsdk.ui.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 17点 06 分
 * @Desc TODO:
 */
class FSIconRadioGroupLayout @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(ctx, attrs, defStyleAttr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        orientation = VERTICAL
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child !is FSIconRadioTextView) {
                removeView(child)
                Log.w(
                    "FSIconRadioGroupLayout",
                    "child must be FSIconRadioTextView, not ${child.javaClass.name}"
                )
            } else {
                child.setOnCheckedChangeListener { v, it ->
                    for (j in 0 until childCount) {
                        val child = getChildAt(j)
                        if (child != v && child is FSIconRadioTextView) {
                            child.isChecked = false
                        }
                    }
                }
            }
        }
    }

    fun getCheckedItem(): FSIconRadioTextView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is FSIconRadioTextView && child.isChecked) {
                return child
            }
        }
        return null
    }

    fun getCheckedItemPosition(): Int {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is FSIconRadioTextView && child.isChecked) {
                return i
            }
        }
        return -1
    }

    fun setCheckedItem(position: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is FSIconRadioTextView) {
                child.isChecked = i == position
            }
        }
    }
}