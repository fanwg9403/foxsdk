package com.sohuglobal.foxsdk.ui.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.databinding.FsDialogLoginBinding
import com.sohuglobal.foxsdk.core.WishFoxEntryActivity
import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager
import com.sohuglobal.foxsdk.ui.view.activity.FSWebActivity
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.custom.CustomLiveData
import com.sohuglobal.foxsdk.utils.custom.CustomTextWatcher
import com.sohuglobal.foxsdk.utils.onClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.Continuation


/**
 * @Author FHL
 * @CreateTime 2025年 10月 13日 16点 22 分
 * @Desc 登录弹窗
 */
class FSLoginDialog(val ctx: Context) : Dialog(ctx, R.style.FSLoadingDialog) {

    init {
        _instance = this
    }

    private val binding by lazy { FsDialogLoginBinding.inflate(layoutInflater) }
    private val phone = CustomLiveData<String>()
    private val verifyCode = CustomLiveData<String>()
    private val password = CustomLiveData<String>()
    private var loading: FSLoadingDialog? = null
    private var mListener: ((arg1: String, arg2: String, type: Int) -> Unit)? = null

    private var timeouter: Timer? = null
    private var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.fsTvVerifyCodeLogin.onClick {
            if (binding.fsTvVerifyCodeLogin.alpha == 0.5f) {
                binding.fsTvVerifyCodeLogin.alpha = 1f
                binding.fsTvPasswordLogin.alpha = 0.5f
                changeLoginType()
            }
        }
        binding.fsTvPasswordLogin.onClick {
            if (binding.fsTvPasswordLogin.alpha == 0.5f) {
                binding.fsTvPasswordLogin.alpha = 1f
                binding.fsTvVerifyCodeLogin.alpha = 0.5f
                changeLoginType()
            }
        }
        binding.fsFlAgree.onClick {
            binding.fsCtvAgree.isChecked = !binding.fsCtvAgree.isChecked
            checkCanLogin()
        }
        binding.fsTvLogin.onClick {
            if (binding.fsTvLogin.alpha != 1f) return@onClick
            loading = FSLoadingDialog(ctx)
            loading?.show()
            if (binding.fsLlVerifyCode.isVisible) {
                mListener?.invoke(phone.value ?: "", verifyCode.value ?: "", 2)
            } else {
                mListener?.invoke(phone.value ?: "", password.value ?: "", 1)
            }
        }
        binding.fsTvSendVerifyCode.onClick {
            if (timeouter != null) return@onClick
            if (phone.value.isNullOrEmpty()) {
                Toaster.show("请输入手机号")
                return@onClick
            } else if (phone.value?.length != 11 || !phone.value!!.matches(Regex("^1[3-9]\\d{9}$"))) {
                Toaster.show("请输入正确的手机号")
                return@onClick
            }
            loading = FSLoadingDialog(ctx)
            loading?.show()
            CoroutineScope(Dispatchers.IO).launch {
                val userName = phone.value ?: ""
                runCatching {
                    FoxSdkRetrofitManager.getApiService().sendSmsCode(
                        mapOf(
                            "channel_id" to WishFoxSdk.getConfig().channelId,
                            "app_id" to WishFoxSdk.getConfig().appId,
                            "user_name" to userName
                        )
                    )
                }.onSuccess {
                    if (it.code == 200) {
                        Toaster.show("验证码发送成功")
                        startTimeout()
                    } else Toaster.show(it.message ?: "验证码发送失败")
                    FoxSdkUtils.runOnUIThread { loading?.dismiss() }
                }.onFailure {
                    Toaster.show(it.message ?: "验证码发送失败")
                    FoxSdkUtils.runOnUIThread { loading?.dismiss() }
                }
            }
        }
        binding.fsEtPhone.setText(phone.value)
        binding.fsEtPassword.setText(password.value)
        binding.fsEtVerifyCode.setText(verifyCode.value)
        binding.fsEtPhone.addTextChangedListener(CustomTextWatcher(phone))
        binding.fsEtVerifyCode.addTextChangedListener(CustomTextWatcher(verifyCode))
        binding.fsEtPassword.addTextChangedListener(CustomTextWatcher(password))
        phone.observe { checkCanLogin() }
        verifyCode.observe { checkCanLogin() }
        password.observe { checkCanLogin() }

//        val installed = FoxSdkUtils.isWishFoxInstalled(ctx)
        binding.fsAuthLogin.isVisible = false
        binding.fsPrimaryLogin.isVisible = true
//        binding.fsIvBack.visibility = if (installed) View.VISIBLE else View.INVISIBLE
        binding.fsIvBack.isVisible = false
        binding.fsVRight.isVisible = false
        binding.fsIvBack.onClick {
//            if (!installed) return@onClick
            binding.fsAuthLogin.isVisible = true
            binding.fsPrimaryLogin.isVisible = false
        }
        binding.fsTvOtherLogin.onClick {
            binding.fsAuthLogin.isVisible = false
            binding.fsPrimaryLogin.isVisible = true
        }
        binding.fsTvAuthLogin.onClick {
            ctx.startActivity(
                Intent(
                    ctx,
                    WishFoxEntryActivity::class.java
                ).setAction(FoxSdkConfig.WishFoxActions.WISH_FOX_AUTH_ACTION)
            )
        }
        binding.fsTvUserAgreement.onClick {
            FSWebActivity.startWithUrl(ctx, "https://world.sohuglobal.com/gameOfUser.html")
        }
        binding.fsTvPrivacyAgreement.onClick {
            FSWebActivity.startWithUrl(ctx, "https://world.sohuglobal.com/gaemOfPrivacy.html")
        }
    }

    private fun startTimeout() {
        if (timeouter != null) return
        time = 60
        timeouter = Timer().apply {
            schedule(object : TimerTask() {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    if (time > 0) {
                        binding.fsTvSendVerifyCode.text = "${time}S"
                        time--
                    } else {
                        binding.fsTvSendVerifyCode.setText(R.string.fs_send_verify_code)
                        timeouter?.cancel()
                        timeouter = null
                    }
                }
            }, 0, 1000)
        }
    }

    private fun checkCanLogin() {
        val phonePass = phone.value?.length == 11
        val verifyCodePass = (verifyCode.value?.length ?: 0) > 3
        val passwordPass = (password.value?.length ?: 0) > 5
        val isAgreement = binding.fsCtvAgree.isChecked
        if (binding.fsLlVerifyCode.isVisible) {
            if (phonePass && verifyCodePass && isAgreement) {
                binding.fsTvLogin.isEnabled = true
                binding.fsTvLogin.alpha = 1f
            } else {
                binding.fsTvLogin.isEnabled = false
                binding.fsTvLogin.alpha = 0.5f
            }
        } else {
            if (phonePass && passwordPass && isAgreement) {
                binding.fsTvLogin.isEnabled = false
                binding.fsTvLogin.alpha = 1f
            } else {
                binding.fsTvLogin.isEnabled = false
                binding.fsTvLogin.alpha = 0.5f
            }
        }
    }

    private fun changeLoginType() {
        binding.fsLlVerifyCode.isVisible = binding.fsTvVerifyCodeLogin.alpha == 1f
        binding.fsLlPassword.isVisible = binding.fsTvPasswordLogin.alpha == 1f
        if (binding.fsLlVerifyCode.isVisible) {
            binding.fsTvLogin.text = ctx.getString(R.string.fs_register_and_login)
        } else {
            binding.fsTvLogin.text = ctx.getString(R.string.fs_login)
        }
    }

    override fun dismiss() {
        super.dismiss()
        loading?.dismiss()
        _instance = null
    }

    /**
     * 登录按钮点击事件
     * @param listener
     * 参数一：手机号
     * 参数二：验证码 或 密码
     * 参数三：登录方式 1 密码登录 2 验证码登录
     */
    fun setOnLoginClickListener(listener: (arg1: String, arg2: String, type: Int) -> Unit) = apply {
        mListener = listener
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var _instance: FSLoginDialog? = null
        fun dismiss() {
            _instance?.dismiss()
        }

        fun dismissLoading() {
            _instance?.loading?.dismiss()
        }
    }
}
