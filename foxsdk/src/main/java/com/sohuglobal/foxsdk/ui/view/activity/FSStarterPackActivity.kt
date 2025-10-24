package com.sohuglobal.foxsdk.ui.view.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.chad.library.adapter4.BaseQuickAdapter
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.Toaster
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.databinding.FsActivityStarterPackBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSStarterPackIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSStarterPackAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSStarterPackViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSStarterPackViewState
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @file FSStarterPackActivity
 * 文件说明：新手礼包
 *
 * @author 王金强
 * @date 2025/10/13 15:29
 */
class FSStarterPackActivity :
    FoxSdkBaseMviActivity<FSStarterPackViewState, FSStarterPackIntent, FsActivityStarterPackBinding>() {
    override val viewModel: FSStarterPackViewModel by viewModels { FoxSdkViewModelFactory() }

    override fun createBinding(): FsActivityStarterPackBinding =
        FsActivityStarterPackBinding.inflate(layoutInflater)

    private val starterPackAdapter by lazy { FSStarterPackAdapter() }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FSStarterPackActivity::class.java))
        }
    }

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
            setRefreshHeader(ClassicsHeader(this@FSStarterPackActivity))
            setRefreshFooter(ClassicsFooter(this@FSStarterPackActivity))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSStarterPackIntent.Refresh())
                    }
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSStarterPackIntent.LoadMore())
                    }
                }

            })
        }

        binding.fsRecyclerView.apply {
            adapter = starterPackAdapter.apply {
                addOnItemChildClickListener(
                    id = (R.id.fs_shp_tv_on),
                    object : BaseQuickAdapter.OnItemChildClickListener<FSStarterPack> {
                        override fun onItemClick(
                            adapter: BaseQuickAdapter<FSStarterPack, *>,
                            view: View,
                            position: Int
                        ) {
                            when (view.id) {
                                R.id.fs_shp_tv_on -> {
                                    viewModel.clicklPostion = position
                                    when (adapter.items[position].status) {
                                        1 -> {
                                            dispatch(FSStarterPackIntent.ReceiveStarterPack(adapter.items[position].id.toString()))

                                        }

                                        else -> {
                                            FoxSdkUtils.copyText(
                                                this@FSStarterPackActivity,
                                                adapter.items[position].code
                                            )
                                            Toaster.show(context.getString(R.string.fs_str_redemption_successful))
                                        }
                                    }
                                }
                            }
                        }

                    })
            }
        }



    }


    /**
     * 根据状态渲染页面
     */
    override fun renderState(state: FSStarterPackViewState) {
        if(state.isInit){
            dispatch(FSStarterPackIntent.LoadInitial())
        }else {
            if (!state.isLoading) {
                if (!state.isRefreshing)
                    binding.fsRefresh.finishRefresh()

                if (!state.isLoadingMore)
                    binding.fsRefresh.finishLoadMore()

                if (state.starterPackList.isEmpty() && state.moreStarterPackList.isEmpty()) {
                    // 空布局
                    starterPackAdapter.isStateViewEnable = true
                    starterPackAdapter.setStateViewLayout(
                        this@FSStarterPackActivity,
                        R.layout.fs_layout_empty
                    )
                } else if (state.starterPackList.isNotEmpty()) {
                    starterPackAdapter.submitList(state.starterPackList)
                } else {
                    starterPackAdapter.addAll(state.moreStarterPackList)
                }

                if (!state.hasMore)
                    binding.fsRefresh.finishLoadMoreWithNoMoreData()
            }
        }


        if (state.isStateViewEnable) {
            if (viewModel.clicklPostion != -1) {
                starterPackAdapter.items[viewModel.clicklPostion].status = 2
                starterPackAdapter.notifyItemChanged(viewModel.clicklPostion)
                FoxSdkUtils.copyText(
                    this@FSStarterPackActivity,
                    starterPackAdapter.items[viewModel.clicklPostion].code
                )
                Toaster.show(this.getString(R.string.fs_str_redemption_successful))
            }
            viewModel.modifyReceiveState()
        }
    }


    override fun finish() {
        binding.fsLlAll.translation("HORIZONTAL", 0, -600, 300)
        binding.root.translation("HORIZONTAL", 0, -600, 300) {
            FoxSdkUtils.runOnUIThreadDelay(50) { super.finish() }
        }
    }
}