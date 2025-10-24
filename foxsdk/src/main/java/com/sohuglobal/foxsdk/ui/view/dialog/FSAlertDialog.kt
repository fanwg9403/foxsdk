package com.sohuglobal.foxsdk.ui.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.databinding.FsAlertDialogBinding
import com.sohuglobal.foxsdk.utils.dp2px

/**
 * @Author FHL
 * @CreateTime 2025年 10月 16日 15点 27 分
 * @Desc 公共弹窗
 */
class FSAlertDialog(ctx: Context) : Dialog(ctx, R.style.FSLoadingDialog) {

    private val binding by lazy { FsAlertDialogBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /**
     * 创建 Builder 对象
     * 弹窗各组件未设置文案时不显示
     */
    class Builder(val ctx: Context) {
        private var title: String? = null

        @ColorInt
        private var titleColor: Int = Color.WHITE

        private var message: String? = null

        @ColorInt
        private var messageColor: Int = Color.WHITE

        private var positiveButtonText: String? = null

        @ColorInt
        private var positiveButtonTextColor: Int = Color.WHITE
        private var positiveButtonClickListener: (() -> Unit)? = null

        private var negativeButtonText: String? = null

        @ColorInt
        private var negativeButtonTextColor: Int = Color.WHITE

        private var negativeButtonClickListener: (() -> Unit)? = null

        private var negativeButtonTheme: String? = null

        private var isCancelable: Boolean = true
        private var openClose: Boolean = false

        private var onDismissListener: (() -> Unit)? = null

        private var contentView: View? = null
        fun setTitle(title: String, @ColorInt color: Int = Color.WHITE) = apply {
            this.title = title
            this.titleColor = color
        }

        fun setTitle(title: Int, @ColorInt color: Int = Color.WHITE) = apply {
            this.title = ctx.getString(title)
            this.titleColor = color
        }

        fun setMessage(message: String, @ColorInt color: Int = Color.WHITE) = apply {
            this.message = message
            this.messageColor = color
        }

        fun setMessage(message: Int, @ColorInt textColor: Int = Color.WHITE) = apply {
            this.message = ctx.getString(message)
            this.messageColor = textColor
        }

        fun setPositive(
            text: String,
            @ColorInt textColor: Int = Color.WHITE,
            listener: (() -> Unit)? = null
        ) = apply {
            this.positiveButtonText = text
            this.positiveButtonClickListener = listener
            this.positiveButtonTextColor = textColor
        }

        fun setPositive(
            text: Int,
            @ColorInt textColor: Int = Color.WHITE,
            listener: (() -> Unit)? = null
        ) = apply {
            this.positiveButtonText = ctx.getString(text)
            this.positiveButtonClickListener = listener
            this.positiveButtonTextColor = textColor
        }

        /**
         * @param theme "fill":常规 "outline": 文字同色外边框
         */
        fun setNegative(
            text: String,
            @ColorInt textColor: Int = Color.WHITE,
            listener: (() -> Unit)? = null,
            theme: String = "fill"
        ) = apply {
            this.negativeButtonText = text
            this.negativeButtonClickListener = listener
            this.negativeButtonTextColor = textColor
            this.negativeButtonTheme = theme
        }

        /**
         * @param theme "fill":常规 "outline": 文字同色外边框
         */
        fun setNegative(
            text: Int,
            @ColorInt textColor: Int = Color.WHITE,
            listener: (() -> Unit)? = null,
            theme: String = "fill"
        ) = apply {
            this.negativeButtonText = ctx.getString(text)
            this.negativeButtonClickListener = listener
            this.negativeButtonTextColor = textColor
            this.negativeButtonTheme = theme
        }

        fun setCancelable(cancelable: Boolean) = apply { isCancelable = cancelable }

        fun setOnDismissListener(listener: () -> Unit) = apply { onDismissListener = listener }

        fun setContentView(view: View) = apply { contentView = view }

        fun setContentView(@LayoutRes layoutId: Int) = apply {
            contentView = LayoutInflater.from(ctx).inflate(layoutId, null)
        }

        fun withClose() = apply {
            openClose = true
        }

        fun build(): FSAlertDialog {
            val dialog = FSAlertDialog(ctx).apply {
                // 标题
                binding.fsTvAlertTitle.isVisible = title != null
                binding.fsTvAlertTitle.text = title
                binding.fsTvAlertTitle.setTextColor(titleColor)

                // 内容
                binding.fsTvAlertMessage.isVisible = message != null
                binding.fsTvAlertMessage.text = message
                binding.fsTvAlertMessage.setTextColor(messageColor)

                if (contentView != null) {
                    binding.fsTvAlertTitle.visibility = View.INVISIBLE
                    binding.fsTvAlertMessage.visibility = View.INVISIBLE
                    binding.fsLlAlertContainer.isVisible = true
                    binding.fsLlAlertContainer.removeAllViews()
                    binding.fsLlAlertContainer.addView(contentView)
                }

                binding.fsIvAlertClose.setOnClickListener { dismiss() }
                binding.fsIvAlertClose.isVisible = openClose

                // 确定按钮
                binding.fsTvConfirm.isVisible = positiveButtonText != null
                binding.fsTvConfirm.text = positiveButtonText
                binding.fsTvConfirm.setTextColor(positiveButtonTextColor)
                binding.fsTvConfirm.setOnClickListener {
                    positiveButtonClickListener?.invoke()
                    dismiss()
                }

                // 取消按钮
                binding.fsTvCancel.isVisible = negativeButtonText != null
                binding.fsTvCancel.text = negativeButtonText
                binding.fsTvCancel.setTextColor(negativeButtonTextColor)
                if (negativeButtonTheme == "outline") {
                    binding.fsTvCancel.setBackgroundColor(Color.TRANSPARENT)
                    binding.fsTvCancel.shapeDrawableBuilder
                        .setStrokeSize(1.dp2px())
                        .setStrokeColor(negativeButtonTextColor)
                        .setRadius(50f)
                        .intoBackground()
                }
                binding.fsTvCancel.setOnClickListener {
                    negativeButtonClickListener?.invoke()
                    dismiss()
                }

                setCancelable(isCancelable)

                setOnDismissListener {
                    onDismissListener?.invoke()
                }
            }

            return dialog
        }
    }
}