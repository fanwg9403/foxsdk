package com.sohuglobal.foxsdk.ui.view.activity

import android.view.View
import androidx.activity.viewModels
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.Toaster
import com.scwang.smart.refresh.header.ClassicsHeader
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.data.model.entity.FSMessage
import com.sohuglobal.foxsdk.databinding.FsActivityMessageBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSMessageIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSMessageListAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSMessageViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSMessageViewState
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @Author FHL
 * @CreateTime 2025年 10月 16日 21点 14 分
 * @Desc 消息中心
 */

class FSMessageActivity :
    FoxSdkBaseMviActivity<FSMessageViewState, FSMessageIntent, FsActivityMessageBinding>() {
    override val viewModel: FSMessageViewModel by viewModels { FoxSdkViewModelFactory() }

    private val mAdapter by lazy {
        FSMessageListAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                val item = adapter.getItem(position)
                showDetail(item)
                if (!item.isRead) dispatch(FSMessageIntent.Read(item.mailId))
            }
        }
    }
    private var _detailShow = false
//    override fun getScreenOrientation() = FoxSdkConfig.ORIENTATION_AUTO

    override fun createBinding() = FsActivityMessageBinding.inflate(layoutInflater)
    override fun initView() {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .transparentStatusBar()
            .statusBarView(binding.fsVTopSafeArea)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        binding.fsMessageRoot.apply {
            post {
                FoxSdkUtils.runOnUIThreadDelay(100) { binding.fsVStartSafeArea.alpha = 1f }
                translation("HORIZONTAL", -600, 0, 300)
                binding.fsVTopSafeArea.translation("HORIZONTAL", -600, 0, 300)
            }
        }

        binding.fsMessageDetail.onClick {  /* 防止点击事件穿透到空白区域触发关闭 */ }
        binding.fsTvDetailBack.onClick { hideDetail() }
        binding.fsVOutside.onClick { finish() }
        binding.fsIvBack.onClick { finish() }
        binding.fsIvClear.onClick { dispatch(FSMessageIntent.Read()) }
        binding.fsRefresh.setRefreshHeader(ClassicsHeader(this))
        binding.fsRefresh.setOnRefreshListener {
            dispatch(FSMessageIntent.Refresh())
        }
    }

    private fun showDetail(item: FSMessage) {
        if (_detailShow) {
            hideDetail {
                showDetail(item)
            }
        } else {
            binding.fsTvDetailTitle.text = item.title
            binding.fsTvDetailContent.text = item.content
            binding.fsTvDetailTime.text = item.time
            _detailShow = true
            binding.fsMessageDetail.visibility = View.VISIBLE
            binding.fsVTopSafeArea2.translation("HORIZONTAL", -600, 0, 300)
            binding.fsMessageDetail.translation("HORIZONTAL", -600, 0, 300)
        }
    }

    override fun renderState(state: FSMessageViewState) {
        when (state) {
            is FSMessageViewState.LoadList -> {
                binding.fsRefresh.finishRefresh()
                if (state.isRefresh) mAdapter.submitList(state.list)
                else mAdapter.addAll(state.list)
            }

            is FSMessageViewState.Read -> {
                if (state.id == null) {
                    val list = mAdapter.items.apply { forEach { it.is_read = 1 } }
                    mAdapter.submitList(list)
                    Toaster.show("清除未读消息成功")
                } else {
                    val data = mAdapter.items.find { it.id == state.id }
                    if (data != null) {
                        val index = mAdapter.items.indexOf(data)
                        data.is_read = 1
                        mAdapter[index] = data
                    }
                }
            }

            is FSMessageViewState.Init -> {
                mAdapter.setStateViewLayout(this, R.layout.fs_layout_empty)
                mAdapter.isStateViewEnable = true
                binding.fsRv.apply { adapter = mAdapter }
                dispatch(FSMessageIntent.Refresh())
            }
        }
    }

    fun hideDetail(call: () -> Unit = {}) {
        binding.fsVTopSafeArea2.translation("HORIZONTAL", 0, -600, 300)
        binding.fsMessageDetail.translation("HORIZONTAL", 0, -600, 300) {
            _detailShow = false
            binding.fsMessageDetail.visibility = View.INVISIBLE
            call()
        }
    }

    override fun finish() {
        if (_detailShow) {
            hideDetail()
        } else {
            binding.fsVTopSafeArea.translation("HORIZONTAL", 0, -600, 300)
            binding.fsMessageRoot.translation("HORIZONTAL", 0, -600, 300) {
                FoxSdkUtils.runOnUIThreadDelay(50) { super.finish() }
            }
        }
    }
}