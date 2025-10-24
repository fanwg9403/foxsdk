package com.sohuglobal.foxsdk.ui.view.activity

import android.util.Log
import androidx.activity.viewModels
import com.google.gson.Gson
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sohuglobal.foxsdk.databinding.FsActivityUserFollowsBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSUserFollowsIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSUserFollowsAdapter
import com.sohuglobal.foxsdk.ui.viewmodel.FSUserFollowsViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSUserFollowsViewState

/**
 *
 * 主要功能: 用户关注列表页面
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 16:32
 */
class FSUserFollowsActivity :
    FoxSdkBaseMviActivity<FSUserFollowsViewState, FSUserFollowsIntent, FsActivityUserFollowsBinding>() {

    companion object {
        private const val USER_ID = "1"
    }

    override val viewModel: FSUserFollowsViewModel by viewModels { FoxSdkViewModelFactory() }

    private val userFollowsAdapter by lazy { FSUserFollowsAdapter() }

    override fun createBinding(): FsActivityUserFollowsBinding =
        FsActivityUserFollowsBinding.inflate(layoutInflater)

    override fun initView() {
        binding.fsRefresh.apply {
            setRefreshHeader(ClassicsHeader(this@FSUserFollowsActivity))
            setRefreshFooter(ClassicsFooter(this@FSUserFollowsActivity))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSUserFollowsIntent.Refresh(USER_ID))
                    }
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    if (!viewModel.viewState.value.isRefreshing &&
                        !viewModel.viewState.value.isLoadingMore &&
                        !viewModel.viewState.value.isLoading
                    ) {
                        dispatch(FSUserFollowsIntent.LoadMore(USER_ID))
                    }
                }

            })
        }

        binding.fsRecyclerView.apply {
            adapter = userFollowsAdapter
        }
    }

    /**
     * 根据状态渲染页面
     */
    override fun renderState(state: FSUserFollowsViewState) {
        if (!state.isRefreshing)
            binding.fsRefresh.finishRefresh()

        if (!state.isLoadingMore)
            binding.fsRefresh.finishLoadMore()

        if (state.users.isEmpty() && state.moreUsers.isEmpty()) {
            // 空布局
        } else if (state.users.isNotEmpty()) {
            userFollowsAdapter.submitList(state.users)
        } else {
            userFollowsAdapter.addAll(state.moreUsers)
        }

        if (!state.hasMore)
            binding.fsRefresh.finishLoadMoreWithNoMoreData()
    }

    override fun onResume() {
        super.onResume()

        // 没有数据时，每次进入页面就拿数据
        if (userFollowsAdapter.itemCount == 0) {
            dispatch(FSUserFollowsIntent.LoadInitial(USER_ID))
        }
    }
}