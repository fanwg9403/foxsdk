package com.sohuglobal.foxsdk.ui.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.chad.library.adapter4.BaseQuickAdapter
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.data.model.entity.FSRechargeRecord
import com.sohuglobal.foxsdk.databinding.FsActivityRechargeRecordBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSRechargeRecordIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSRechargeRecordAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSRechargeRecordViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSRechargeRecordViewState
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @file FSRechargeRecordActivity
 * 文件说明：充值记录
 *
 * @author 王金强
 * @date 2025/10/16 18:23
 */
class FSRechargeRecordActivity :
    FoxSdkBaseMviActivity<FSRechargeRecordViewState, FSRechargeRecordIntent, FsActivityRechargeRecordBinding>() {
    override val viewModel: FSRechargeRecordViewModel by viewModels { FoxSdkViewModelFactory() }

    private val rechargeRecordAdapter by lazy { FSRechargeRecordAdapter() }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FSRechargeRecordActivity::class.java))
        }
    }
//    override fun getScreenOrientation() = FoxSdkConfig.ORIENTATION_AUTO

    override fun createBinding(): FsActivityRechargeRecordBinding =
        FsActivityRechargeRecordBinding.inflate(layoutInflater)

    override fun initView() {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .statusBarView(binding.fsTopView)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .transparentBar()
            .init()
        binding.root.apply {
            post {
                translation("HORIZONTAL", -600, 0, 300)
                binding.fsLlAll.translation("HORIZONTAL", -600, 0, 300)
            }
        }
        binding.fsLlBack.onClick {
            finish()
        }
        binding.fsLlAll.onClick { }
        binding.root.onClick {
            finish()
        }

        binding.fsRefresh.apply {
            setRefreshHeader(ClassicsHeader(this@FSRechargeRecordActivity))
            setRefreshFooter(ClassicsFooter(this@FSRechargeRecordActivity))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSRechargeRecordIntent.Refresh())
                    }
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSRechargeRecordIntent.LoadMore())
                    }
                }

            })
        }
        binding.fsRecyclerView.apply {
            adapter = rechargeRecordAdapter.apply {
                addOnItemChildClickListener(
                    id = (R.id.fs_shp_tv_on),
                    object : BaseQuickAdapter.OnItemChildClickListener<FSRechargeRecord> {
                        override fun onItemClick(
                            adapter: BaseQuickAdapter<FSRechargeRecord, *>,
                            view: View,
                            position: Int
                        ) {

                        }

                    })
            }
        }


    }

    override fun renderState(state: FSRechargeRecordViewState) {
        if(state.isInit){
            dispatch(FSRechargeRecordIntent.LoadInitial())
        }else{
            if(!state.isLoading){
                if (!state.isRefreshing)
                    binding.fsRefresh.finishRefresh()

                if (!state.isLoadingMore)
                    binding.fsRefresh.finishLoadMore()

                if (state.rechargeRecordList.isEmpty() && state.moreRechargeRecordList.isEmpty()) {
                    // 空布局
                    rechargeRecordAdapter.isStateViewEnable = true
                    rechargeRecordAdapter.setStateViewLayout(
                        this@FSRechargeRecordActivity,
                        R.layout.fs_layout_empty
                    )
                } else if (state.rechargeRecordList.isNotEmpty()) {
                    rechargeRecordAdapter.submitList(state.rechargeRecordList)
                } else {
                    rechargeRecordAdapter.addAll(state.moreRechargeRecordList)
                }

                if (!state.hasMore)
                    binding.fsRefresh.finishLoadMoreWithNoMoreData()
            }
        }

    }

    override fun finish() {
        binding.fsLlAll.translation("HORIZONTAL", 0, -600, 300)
        binding.root.translation("HORIZONTAL", 0, -600, 300) {
            FoxSdkUtils.runOnUIThreadDelay(50) { super.finish() }
        }
    }
}