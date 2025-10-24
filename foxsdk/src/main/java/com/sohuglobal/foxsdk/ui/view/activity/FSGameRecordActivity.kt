package com.sohuglobal.foxsdk.ui.view.activity

import android.content.Context
import android.content.Intent
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
import com.sohuglobal.foxsdk.data.model.entity.FSGameRecord
import com.sohuglobal.foxsdk.databinding.FsActivityGameRecordBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSGameRecordIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSGameRecordAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSGameRecordViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSGameRecordViewState
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @file FSGameRecordActivity
 * 文件说明：游戏记录
 *
 * @author 王金强
 * @date 2025/10/17 9:39
 */
class FSGameRecordActivity :
    FoxSdkBaseMviActivity<FSGameRecordViewState, FSGameRecordIntent, FsActivityGameRecordBinding>() {
    override val viewModel: FSGameRecordViewModel by viewModels { FoxSdkViewModelFactory() }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FSGameRecordActivity::class.java))
        }
    }

    private val gameRecordAdapter: FSGameRecordAdapter by lazy {
        FSGameRecordAdapter()
    }

    override fun createBinding(): FsActivityGameRecordBinding =
        FsActivityGameRecordBinding.inflate(layoutInflater)


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
            setRefreshHeader(ClassicsHeader(this@FSGameRecordActivity))
            setRefreshFooter(ClassicsFooter(this@FSGameRecordActivity))
            setEnableLoadMore(false)
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSGameRecordIntent.Refresh())
                    }
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {

                }

            })
        }
        binding.fsRecyclerView.apply {
            adapter = gameRecordAdapter.apply {
                addOnItemChildClickListener(
                    id = (R.id.fs_shp_tv_on),
                    object : BaseQuickAdapter.OnItemChildClickListener<FSGameRecord> {
                        override fun onItemClick(
                            adapter: BaseQuickAdapter<FSGameRecord, *>,
                            view: View,
                            position: Int
                        ) {

                        }

                    })
            }
        }
    }

    override fun renderState(state: FSGameRecordViewState) {
        if(state.isInit){
            dispatch(FSGameRecordIntent.LoadInitial())
        }else{
            if(!state.isLoading){
                if (!state.isRefreshing)
                    binding.fsRefresh.finishRefresh()

                if (!state.isLoadingMore)
                    binding.fsRefresh.finishLoadMore()

                if (state.gameRecordList.isEmpty() && state.moreGameRecordList.isEmpty()) {
                    // 空布局
                    gameRecordAdapter.isStateViewEnable = true
                    gameRecordAdapter.setStateViewLayout(
                        this@FSGameRecordActivity,
                        R.layout.fs_layout_empty
                    )
                } else if (state.gameRecordList.isNotEmpty()) {
                    gameRecordAdapter.submitList(state.gameRecordList)
                } else {
                    gameRecordAdapter.addAll(state.moreGameRecordList)
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