package com.sohuglobal.foxsdk.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.domain.intent.FoxSdkViewIntent
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkUiEffect
import com.sohuglobal.foxsdk.ui.viewstate.FoxSdkViewState
import kotlinx.coroutines.launch

/**
 *
 * 主要功能: MVI架构的BaseFragment
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 14:08
 */
abstract class FoxSdkBaseMviFragment<VS : FoxSdkViewState, VI : FoxSdkViewIntent, VB : ViewBinding> :
    Fragment() {

    protected abstract val viewModel: FoxSdkBaseMviViewModel<VS, VI, FoxSdkUiEffect>
    protected lateinit var binding: VB

    // Loading
    private var loadingDialog: FSLoadingDialog? = null
    private var isLoadingShowing = false

    /**
     * 创建ViewBinding
     */
    protected abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeState()
        observeEffects()
        observeLoading()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 确保在Fragment销毁时隐藏Loading
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
        viewLifecycleOwner.lifecycleScope.launch {
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
        viewLifecycleOwner.lifecycleScope.launch {
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
        viewLifecycleOwner.lifecycleScope.launch {
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
            is FoxSdkUiEffect.NavigateBack -> requireActivity().onBackPressed()
        }
    }

    /**
     * 显示Loading - 生命周期安全
     */
    protected open fun showLoading(message: String? = null) {
        if (isAdded && !isRemoving && !isLoadingShowing) {
            loadingDialog = FSLoadingDialog(requireContext()).apply {
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
}