package com.sohuglobal.foxsdk.ui.view.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.databinding.FsIconRadioTextViewBinding
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 16点 39 分
 * @Desc TODO:
 */
class FSIconRadioTextView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(ctx, attrs, defStyleAttr) {

    private var _isChecked = false
    private var text = ""
    private var textColor = -1
    private var icon = -1

    private var checkedRadioColor = -1
    private var radioColor = -1

    private var _disabled = false

    init {
        if (attrs != null) {
            ctx.withStyledAttributes(attrs, R.styleable.FSIconRadioTextView) {
                text = getString(R.styleable.FSIconRadioTextView_android_text) ?: ""
                textColor = getColor(R.styleable.FSIconRadioTextView_android_textColor, -1)
                icon = getResourceId(R.styleable.FSIconRadioTextView_android_icon, -1)
                radioColor = getColor(R.styleable.FSIconRadioTextView_radioColor, -1)
                checkedRadioColor = getColor(R.styleable.FSIconRadioTextView_checkedRadioColor, -1)
                _isChecked = getBoolean(R.styleable.FSIconRadioTextView_android_checked, false)
                _disabled = !getBoolean(R.styleable.FSIconRadioTextView_android_enabled, true)
            }
        }
    }

    private val binding = FsIconRadioTextViewBinding.inflate(
        LayoutInflater.from(ctx), this, true
    )

    private var listener: ((Boolean) -> Unit)? = null
    private var viewListener: ((View, Boolean) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        if (icon != -1) {
            binding.fsCheckRadioIcon.setImageResource(icon)
        } else {
            binding.fsCheckRadioIcon.visibility = GONE
        }

        if (text.isNotEmpty()) {
            binding.fsCheckRadioText.text = text
        } else {
            binding.fsCheckRadioText.visibility = GONE
        }
        if (textColor != -1) {
            binding.fsCheckRadioText.setTextColor(textColor)
        }
        if (radioColor != -1 && !_isChecked) {
            binding.fsCheckRadio.backgroundTintList = ColorStateList.valueOf(radioColor)
        }
        if (checkedRadioColor != -1 && _isChecked) {
            binding.fsCheckRadio.backgroundTintList = ColorStateList.valueOf(checkedRadioColor)
        }
        binding.fsCheckRadio.isChecked = _isChecked
        binding.root.setOnClickListener {
            if (_disabled) return@setOnClickListener
            isChecked = !_isChecked
            listener?.invoke(_isChecked)
            viewListener?.invoke(this, _isChecked)
        }
        if (_disabled) {
            binding.fsCheckRadio.setBackgroundResource(R.drawable.fs_radio_disabled)
            binding.fsCheckRadioText.setTextColor("#66FFFFFF".toColorInt())
            binding.fsCheckRadio.isChecked = false
        }
    }

    fun setText(text: String) {
        binding.fsCheckRadioText.text = text
    }

    fun setTextColor(color: Int) {
        binding.fsCheckRadioText.setTextColor(color)
    }

    fun setIcon(icon: Int) {
        binding.fsCheckRadioIcon.setImageResource(icon)
    }

    fun setIcon(icon: Drawable) {
        binding.fsCheckRadioIcon.setImageDrawable(icon)
    }

    fun setRadioColor(color: Int) {
        radioColor = color
        if (!_isChecked) {
            binding.fsCheckRadio.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    fun setCheckedRadioColor(color: Int) {
        checkedRadioColor = color
        if (_isChecked) {
            binding.fsCheckRadio.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    fun setText(@StringRes text: Int) {
        binding.fsCheckRadioText.setText(text)
    }

    var isChecked: Boolean
        get() = _isChecked
        set(value) {
            _isChecked = value
            binding.fsCheckRadio.isChecked = value
            if (value) {
                binding.fsCheckRadio.backgroundTintList =
                    if (checkedRadioColor != -1) ColorStateList.valueOf(checkedRadioColor)
                    else null
            } else {
                binding.fsCheckRadio.backgroundTintList =
                    if (radioColor != -1) ColorStateList.valueOf(radioColor)
                    else null
            }
        }

    fun setOnCheckedChangeListener(listener: (View, Boolean) -> Unit) {
        this.viewListener = listener
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        _disabled = !enabled
        if (_disabled) {
            isChecked = false
            binding.fsCheckRadio.setBackgroundResource(R.drawable.fs_radio_disabled)
            binding.fsCheckRadioText.setTextColor("#66FFFFFF".toColorInt())
        } else {
            binding.fsCheckRadio.setBackgroundResource(R.drawable.fs_radio_selector)
            binding.fsCheckRadioText.setTextColor("#FFFFFFFF".toColorInt())
        }
    }
}