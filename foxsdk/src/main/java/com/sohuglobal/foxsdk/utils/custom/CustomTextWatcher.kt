package com.sohuglobal.foxsdk.utils.custom

import android.text.Editable
import android.text.TextWatcher

/**
 * @Author FHL
 * @CreateTime 2025年 10月 13日 16点 47 分
 * @Desc 自定义 TextWatcher，减少代码行数
 */
open class CustomTextWatcher(val liveData: CustomLiveData<String>? = null) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
        liveData?.postValue(p0?.toString() ?: "")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}