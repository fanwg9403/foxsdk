package com.sohuglobal.foxsdk.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.domain.intent.FoxSdkViewIntent
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkViewState
import kotlinx.coroutines.launch

/**
 *
 * 主要功能: MVI架构的BaseActivity
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 14:08
 */
abstract class FoxSdkBaseMviActivity<VS : FoxSdkViewState, VI : FoxSdkViewIntent, VB : ViewBinding> :
    AppCompatActivity() {

    protected abstract val viewModel: FoxSdkBaseMviViewModel<VS, VI, FoxSdkUiEffect>
    protected lateinit var binding: VB

    protected open fun getScreenOrientation(): Int = WishFoxSdk.getConfig().screenOrientation

    // Loading
    private var loadingDialog: FSLoadingDialog? = null
    private var isLoadingShowing = false

    /**
     * 创建ViewBinding
     */
    protected abstract fun createBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        applyScreenOrientation()
        super.onCreate(savedInstanceState)
        binding = createBinding()
        setContentView(binding.root)

        initView()
        observeState()
        observeEffects()
        observeLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保在Activity销毁时隐藏Loading
        dismissLoading()
    }

    /**
     * 初始化视图
     */
    protected abstract fun initView()

    /**
     * 观察状态变化
     */
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    /**
     * 观察UI效果
     */
    private fun observeEffects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    handleEffect(effect)
                }
            }
        }
    }

    /**
     * 观察Loading状态 - 结合生命周期避免内存泄漏
     */
    private fun observeLoading() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collect { loadingState ->
                    when (loadingState) {
                        is LoadingState.Show -> showLoading(loadingState.message)
                        LoadingState.Dismiss -> dismissLoading()
                        LoadingState.Idle -> { /* 什么都不做 */
                        }
                    }
                }
            }
        }
    }

    /**
     * 渲染状态
     */
    protected abstract fun renderState(state: VS)

    /**
     * 处理UI效果
     */
    protected open fun handleEffect(effect: FoxSdkUiEffect) {
        when (effect) {
            is FoxSdkUiEffect.ShowToast -> Toaster.show(effect.message)
            is FoxSdkUiEffect.NavigateTo -> navigateTo(effect.destination, effect.args)
            is FoxSdkUiEffect.NavigateBack -> onBackPressed()
        }
    }

    /**
     * 显示Loading - 生命周期安全
     */
    protected open fun showLoading(message: String? = null) {
        if (!isFinishing && !isDestroyed && !isLoadingShowing) {
            loadingDialog = FSLoadingDialog(this).apply {
                setMessage(message)
                setCancelable(false)
                setOnDismissListener {
                    isLoadingShowing = false
                }
            }
            loadingDialog?.show()
            isLoadingShowing = true
        }
    }

    /**
     * 隐藏Loading - 生命周期安全
     */
    protected open fun dismissLoading() {
        if (isLoadingShowing) {
            loadingDialog?.dismiss()
            loadingDialog = null
            isLoadingShowing = false
        }
    }

    /**
     * 发送意图
     */
    protected fun dispatch(intent: VI) {
        viewModel.dispatch(intent)
    }

    /**
     * 导航 - 子类需要实现具体的导航逻辑
     */
    protected open fun navigateTo(destination: String, args: Bundle?) {
        // 子类实现具体导航逻辑
    }

    /**
     *
     */
    private fun applyScreenOrientation() {
        when (getScreenOrientation()) {
            FoxSdkConfig.ORIENTATION_PORTRAIT -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            FoxSdkConfig.ORIENTATION_LANDSCAPE -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            FoxSdkConfig.ORIENTATION_AUTO -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
    }
}