package com.sohuglobal.foxsdk.ui.view.activity

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.chad.library.adapter4.QuickAdapterHelper
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.header.ClassicsHeader
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import com.sohuglobal.foxsdk.databinding.FsActivityHomeBinding
import com.sohuglobal.foxsdk.di.FoxSdkViewModelFactory
import com.sohuglobal.foxsdk.domain.intent.FSHomeIntent
import com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity
import com.sohuglobal.foxsdk.ui.view.adapter.FSHomeActionAdapter
import com.sohuglobal.foxsdk.ui.view.adapter.FSHomeBannerAdapter
import com.sohuglobal.foxsdk.ui.view.adapter.FSHomeRegionAdapter
import com.sohuglobal.foxsdk.ui.view.adapter.FSHomeUserInfoAdapter
import com.sohuglobal.foxsdk.ui.view.dialog.FSAlertDialog
import com.sohuglobal.foxsdk.ui.viewmodel.FSHomeViewModel
import com.sohuglobal.foxsdk.ui.viewstate.FSHomeViewState
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.translation

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 09点 20 分
 * @Desc 游戏sdk主界面
 */
class FSHomeActivity :
    FoxSdkBaseMviActivity<FSHomeViewState, FSHomeIntent, FsActivityHomeBinding>() {
    override val viewModel: FSHomeViewModel by viewModels { FoxSdkViewModelFactory() }

    private val userInfoAdapter by lazy { FSHomeUserInfoAdapter(viewModel) }
    private val bannerAdapter by lazy { FSHomeBannerAdapter() }
    private val regionAdapter by lazy { FSHomeRegionAdapter(viewModel) }
    private val actionAdapter by lazy {
        FSHomeActionAdapter(
            listOf(
                Pair("游戏记录", 0),
                Pair("充值记录", 1),
                Pair("我的消息", 2),
                Pair("", -1)
            )
        ) { it ->
            Log.d("FoxSdk", "点击了：${it}")
            when (it) {
                0 -> {
                    // TODO: 2025/10/14 跳转游戏记录
                    FSGameRecordActivity.start(this)
                }

                1 -> {
                    // TODO: 2025/10/14 跳转充值记录
                    FSRechargeRecordActivity.start(this)
                }

                2 -> {
                    startActivity(Intent(this, FSMessageActivity::class.java))
                }

                3 -> {

//                    FSPayDialog(this)
//                        .setPayName("狐币充值")
//                        .setPayInfo("10元/1000狐币")
//                        .setPayType(FoxSdkPayEnum.FOX_COIN)
//                        .setDisableFoxCoinPay(true)
//                        .setOnConfirm {
//                            when (it) {
//                                FoxSdkPayEnum.FOX_COIN -> {
//                                    Toaster.show("狐币支付")
//                                }
//
//                                FoxSdkPayEnum.WECHAT -> {
//                                    Toaster.show("微信支付")
//                                }
//
//                                FoxSdkPayEnum.ALI_PAY -> {
//                                    Toaster.show("支付宝支付")
//                                }
//                            }
//                        }.show()

//                    FSAlertDialog.Builder(this)
//                        .setContentView(R.layout.fs_layout_pay_success)
//                        .setPositive("继续游戏") {}
//                        .withClose()
//                        .build()
//                        .show()

//                    FSAlertDialog.Builder(this)
//                        .setContentView(R.layout.fs_layout_pay_failed)
//                        .setPositive("重新购买") {}
//                        .setNegative(
//                            "返回游戏",
//                            textColor = resources.getColor(R.color.fs_primary),
//                            theme = "outline"
//                        )
//                        .withClose()
//                        .build()
//                        .show()

                    FSAlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要退出登录吗？")
                        .setPositive("确定") { viewModel.dispatch(FSHomeIntent.Logout()) }
                        .setNegative("取消")
                        .build()
                        .show()
                }
            }
        }
    }

    override fun getScreenOrientation() = FoxSdkConfig.ORIENTATION_AUTO

    private val adapterHelper by lazy { QuickAdapterHelper.Builder(userInfoAdapter).build() }

    override fun createBinding() = FsActivityHomeBinding.inflate(layoutInflater)

    override fun initView() {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .transparentStatusBar()
            .statusBarView(binding.fsVTopSafeArea)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()

        binding.fsHomeRoot.apply {
            post {
                translation("HORIZONTAL", -600, 0, 300)
                binding.fsVTopSafeArea.translation("HORIZONTAL", -600, 0, 300)
                FoxSdkUtils.runOnUIThreadDelay(100) { binding.fsVStartSafeArea.alpha = 1f }
            }
        }

        adapterHelper.addAfterAdapter(bannerAdapter)
        adapterHelper.addAfterAdapter(regionAdapter)

        binding.fsRv.apply {
            setAdapter(adapterHelper.adapter)
        }

        binding.fsVOutside.onClick { finish() }
        binding.fsHomeRoot.isEnabled = true
        binding.fsHomeRoot.setRefreshHeader(ClassicsHeader(this))
        dispatch(FSHomeIntent.Init())
        binding.fsHomeRoot.setOnRefreshListener {
            dispatch(FSHomeIntent.Init())
        }
    }

    override fun renderState(state: FSHomeViewState) {
        binding.fsHomeRoot.finishRefresh()
        if (state.userInfo != null && state.loginSuccess) {
            if (state.coinInfo != null) {
                state.userInfo.foxCoin = (state.coinInfo.balanceCoin?.toFloat())?.toInt() ?: 0
            }
            // 已登录
            userInfoAdapter.item = state.userInfo
            adapterHelper.addAfterAdapter(actionAdapter)

        } else {
            // 未登录
            userInfoAdapter.item = null
            if (adapterHelper.afterAdapterList.contains(actionAdapter)) {
                adapterHelper.removeAdapter(actionAdapter)
            }
        }

        bannerAdapter.item = state.bannerList
    }

    override fun finish() {
        FoxSdkUtils.runOnUIThreadDelay(200) { binding.fsVStartSafeArea.alpha = 0f }
        binding.fsVTopSafeArea.translation("HORIZONTAL", 0, -600, 300)
        binding.fsHomeRoot.translation("HORIZONTAL", 0, -600, 300) {
            FoxSdkUtils.runOnUIThreadDelay(50) { super.finish() }
        }
    }
}