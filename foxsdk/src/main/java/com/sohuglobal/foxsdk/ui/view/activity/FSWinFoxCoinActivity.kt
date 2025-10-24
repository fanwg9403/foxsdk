package com.sohuglobal.foxsdk.ui.view.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.chad.library.adapter4.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.data.model.entity.FSGameSchemeData
import com.sohuglobal.foxsdk.data.model.entity.FSWinFoxCoin
import com.sohuglobal.foxsdk.databinding.FsActivityWinFoxCoinBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSWinFoxCoinIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSWinFoxCoinAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSWinFoxCoinViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSWinFoxCoinViewState
import com.sohuglobal.foxsdk.utils.FoxSdkAppJumpUtils
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @file FSWinFoxCoinActivity
 * 文件说明：赢狐币
 *
 * @author 王金强
 * @date 2025/10/14 14:33
 */
class FSWinFoxCoinActivity :
    FoxSdkBaseMviActivity<FSWinFoxCoinViewState, FSWinFoxCoinIntent, FsActivityWinFoxCoinBinding>() {
    override val viewModel: FSWinFoxCoinViewModel by viewModels { FoxSdkViewModelFactory() }

    override fun createBinding(): FsActivityWinFoxCoinBinding =
        FsActivityWinFoxCoinBinding.inflate(layoutInflater)

    private val winFoxCoinAdapter by lazy { FSWinFoxCoinAdapter() }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FSWinFoxCoinActivity::class.java))
        }
    }

    override fun getScreenOrientation() = FoxSdkConfig.ORIENTATION_AUTO
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
            setRefreshHeader(ClassicsHeader(this@FSWinFoxCoinActivity))
            setRefreshFooter(ClassicsFooter(this@FSWinFoxCoinActivity))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSWinFoxCoinIntent.Refresh())
                    }
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSWinFoxCoinIntent.LoadMore())
                    }
                }

            })
        }

        binding.fsRecyclerView.apply {
            adapter = winFoxCoinAdapter.apply {
                addOnItemChildClickListener(
                    id = (R.id.fs_shp_tv_on),
                    object : BaseQuickAdapter.OnItemChildClickListener<FSWinFoxCoin> {
                        override fun onItemClick(
                            adapter: BaseQuickAdapter<FSWinFoxCoin, *>,
                            view: View,
                            position: Int
                        ) {
                            when (view.id) {
                                R.id.fs_shp_tv_on -> {
                                    val gson = Gson()
                                    val gameSchemeData = FSGameSchemeData(
                                        "wish_game",
                                        adapter.items[position].task_number,
                                        adapter.items[position].type_name
                                    )
                                    val json = gson.toJson(gameSchemeData)
                                    FoxSdkAppJumpUtils.launchByDeepLink(
                                        this@FSWinFoxCoinActivity,
                                        "sohugloba://app/game_sdk?gameData=${json}",adapter.items[position].h5_url)
                                }
                            }
                        }

                    })
            }
        }

    }

    override fun renderState(state: FSWinFoxCoinViewState) {
        if(state.isInit){
            dispatch(FSWinFoxCoinIntent.LoadInitial())
        }else{
            if(!state.isLoading){
                if (!state.isRefreshing)
                    binding.fsRefresh.finishRefresh()

                if (!state.isLoadingMore)
                    binding.fsRefresh.finishLoadMore()

                if (state.winFoxCoinList.isEmpty() && state.moreWinFoxCoinList.isEmpty()) {
                    // 空布局
                    winFoxCoinAdapter.isStateViewEnable = true
                    winFoxCoinAdapter.setStateViewLayout(
                        this@FSWinFoxCoinActivity,
                        R.layout.fs_layout_empty
                    )
                } else if (state.winFoxCoinList.isNotEmpty()) {
                    winFoxCoinAdapter.submitList(state.winFoxCoinList)
                } else {
                    winFoxCoinAdapter.addAll(state.winFoxCoinList)
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