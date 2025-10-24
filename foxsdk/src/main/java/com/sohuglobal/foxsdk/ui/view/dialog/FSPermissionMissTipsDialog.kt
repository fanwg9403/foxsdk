package com.sohuglobal.foxsdk.ui.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.sohuglobal.foxsdk.R

@SuppressLint("ViewConstructor")
class FSPermissionMissTipsDialog(context: Context, private var message: String) : CenterPopupView(context) {

    var listener: OnClickListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.fs_dialog_permission_miss_tips
    }

    //设置监听
    fun setMListener(listener: OnClickListener) {
        this.listener = listener
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        val ok = findViewById<TextView>(R.id.fs_tv_ok)
        val cancel = findViewById<TextView>(R.id.fs_tv_cancel)
        val text = findViewById<TextView>(R.id.fs_text)

        text.text = message

        ok.setOnClickListener {
            dismiss()
            listener?.onClick(it)
        }

        cancel.setOnClickListener {
            dismiss()
        }
    }

}