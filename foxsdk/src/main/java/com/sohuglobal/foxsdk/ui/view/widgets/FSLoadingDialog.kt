package com.sohuglobal.foxsdk.ui.view.widgets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.sohuglobal.foxsdk.R
import java.lang.Exception

/**
 *
 * 主要功能: 统一Loading
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 14:32
 */
class FSLoadingDialog(context: Context) : Dialog(context, R.style.FSLoadingDialog) {

    companion object {
        private const val TAG = "FoxSdk[LoadingDialog]"
    }

    private lateinit var tvMessage: TextView

    init {
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.fs_dialog_loading, null)
        setContentView(view)

        tvMessage = view.findViewById(R.id.fs_tv_message)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setMessage(message: String? = null) {
        if (message.isNullOrEmpty()) {
            tvMessage.isVisible = false
        } else {
            tvMessage.isVisible = true
            tvMessage.text = message
        }
    }

    override fun show() {
        val context = context
        if (context is Activity) {
            if (!context.isFinishing && !context.isDestroyed) {
                try {
                    super.show()
                } catch (e: Exception) {
                    Log.e(TAG, "LoadingDialog 展示失败", e)
                }
            }
        } else {
            try {
                super.show()
            } catch (e: Exception) {
                Log.e(TAG, "LoadingDialog 展示失败", e)
            }
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "LoadingDialog 隐藏失败", e)
        }
    }
}