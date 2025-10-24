package com.sohuglobal.foxsdk.utils

import android.app.Activity
import android.app.Application
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSLoginResult
import com.sohuglobal.foxsdk.data.model.entity.FSPayResult
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile
import com.sohuglobal.foxsdk.data.model.network.FoxSdkNetworkResult
import com.sohuglobal.foxsdk.data.repository.FSHomeRepository
import com.sohuglobal.foxsdk.ui.view.dialog.FSAlertDialog
import com.sohuglobal.foxsdk.ui.view.dialog.FSLoginDialog
import com.sohuglobal.foxsdk.ui.view.dialog.FSPayDialog
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import com.sohuglobal.foxsdk.utils.pay.FoxSdkPayResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @file FoxSdkLongingPayUtils
 * 文件说明：登录支付工具类
 *
 * @author 王金强
 * @date 2025/10/22 14:43
 */
class FoxSdkLongingPayUtils {

    var loading: FSLoadingDialog? = null
    var loginDialog: FSLoginDialog? = null


    var mallIdFS: String = ""
    var mallNameFS: String = ""
    var priceFS: String = ""
    var priceContentFS: String = ""
    var orderTimeFS: Long = 0
    var cpOrderIdFS: String = ""
    /**
     * 登录支付
     * @param mActivity 上下文
     * @param mallId 商品ID
     * @param mallName 商品名称
     * @param price 金额
     * @param priceContent 金额描述带有金额单位
     * @param orderTime 下单时间
     * @param cpOrderId 游戏方订单ID
     */
    fun loginPay(
        mActivity: Activity, mallId: String, mallName: String, price: String, priceContent: String,
        orderTime: Long, cpOrderId: String
    ) {
        mallIdFS = mallId
        mallNameFS = mallName
        priceFS = price
        priceContentFS = priceContent
        orderTimeFS = orderTime
        cpOrderIdFS = cpOrderId
        if (FSUserInfo.getInstance() == null) {//未登录
            loginDialog = FSLoginDialog(mActivity)
                .setOnLoginClickListener { arg1, arg2, type ->
                    loading = FSLoadingDialog(mActivity)
                    loading?.show()
                    // 使用 CoroutineScope 创建新的作用域
                    CoroutineScope(Dispatchers.IO).launch {
                        Loging(arg1, arg2, type) {
                            if (it) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    // 在这里执行需要 Looper 的操作
                                    loginDialog?.dismiss()
                                    loading?.dismiss()
                                    payDialog(
                                        mActivity,
                                        mallId,
                                        mallName,
                                        price,
                                        priceContent,
                                        orderTime,
                                        cpOrderId
                                    ) {
                                    }
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    loading?.dismiss()
                                }
                            }
                        }
                    }

                }
            loginDialog?.show()
        } else {//已登录调用支付
            payDialog(mActivity, mallId, mallName, price, priceContent, orderTime, cpOrderId) {

            }
        }
    }


    /**
     * 登录
     * @param phone 手机号
     * @param code 验证码
     * @param type 登录方式
     * @param LogingListener 登录结果回调
     */
    private suspend fun Loging(
        phone: String,
        code: String,
        type: Int,
        LogingListener: (Boolean) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val result = if (type == 1) {
                FSHomeRepository().loginByPassword(phone, code)
            } else {
                FSHomeRepository().loginByVerifyCode(phone, code)
            }
            if (result is FoxSdkNetworkResult.Success) {
                FSLoginResult.save(result.data)
                FoxSdkSPUtils.instance.setSync(FoxSdkConstant.AUTHORIZATION, result.data.token)
                getUserInfo(LogingListener)
            } else if (result is FoxSdkNetworkResult.Error) {
                Toaster.show(result.error)
            }
        }
    }

    /**
     * 获取用户信息
     * @param LogingListener 登录结果回调
     */
    private suspend fun getUserInfo(LogingListener: (Boolean) -> Unit) {
        val result = FSHomeRepository().getUserInfo()
        if (result is FoxSdkNetworkResult.Success) {
            FSUserProfile.save(result.data)
            val res = FSHomeRepository().getUserVirtualInfo()
            var coinInfo: FSCoinInfo? = null
            if (res is FoxSdkNetworkResult.Success) {
                FSCoinInfo.save(res.data)
                coinInfo = res.data
            }
            Toaster.show("登录成功")
            LogingListener.invoke(true)
        } else if (result is FoxSdkNetworkResult.Error) {
            LogingListener.invoke(false)
            Toaster.show(result.error)
        }
    }

    //支付回调参数
    var fsPayResult: FSPayResult? = null
    //支付弹框
    fun payDialog(
        context: Activity, mallId: String, mallName: String, price: String, priceContent: String,
        orderTime: Long, cpOrderId: String, payListener: (FSPayResult) -> Unit
    ) {
        /**
         * 设置支付下单参数
         * mall_id  商品ID
         * mall_name 商品名称
         * price 金额
         * priceContent 金额描述带有金额单位
         * order_time 下单时间
         * cp_order_id 游戏方订单ID
         */
        FSPayDialog(context)
            .setPayName(mallName)
            .setPayInfo(priceContent)
            .setPayType(FoxSdkPayEnum.FOX_COIN)
            .setPayParams(mallId, mallName, price, orderTime, cpOrderId)
            .setOnPayCreate { payResult ->
                //调用查询支付结果，根据需求在适当位置进行调用
                payListener.invoke(payResult)
                fsPayResult = payResult
                if(fsPayResult?.payType==FoxSdkPayEnum.FOX_COIN){
                    fsPayResult?.let {
                        if (it.isCheckPay) {
                            CoroutineScope(Dispatchers.Main).launch {
                                startPollingPaymentResult(context, it)
                            }
                        }
                    }
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
                    } else {
                        context.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
                    }
                }
            }.show()
    }


    private var pollingJob: Job? = null

    private fun startPollingPaymentResult(context: Activity, payResult: FSPayResult) {
        loading = FSLoadingDialog(context)
        loading?.show()
        // 禁止点击外部关闭
        loading?.setCanceledOnTouchOutside(false)
        // 禁止返回键关闭
        loading?.setCancelable(false)
        loading?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // 返回true表示消费掉返回键事件，这样对话框就不会关闭
                    return true
                }
                return false
            }
        })
        pollingJob = CoroutineScope(Dispatchers.Main).launch {
            repeat(10) { attempt ->
                val success = withContext(Dispatchers.IO) {
                    // 将回调转换为挂起函数
                    suspendCoroutine<Boolean> { continuation ->
                        FoxSdkPayResult.queryResult(context, payResult.orderId) { result ->
                            continuation.resume(result)
                        }
                    }
                }

                if (success) {
                    loading?.dismiss()
                    FSAlertDialog.Builder(context)
                        .setContentView(R.layout.fs_layout_pay_success)
                        .setPositive("继续游戏") {

                        }
                        .setCancelable(false)
                        .withClose()
                        .setOnDismissListener {
                            if(fsPayResult?.payType==FoxSdkPayEnum.FOX_COIN){
                                return@setOnDismissListener
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                context.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
                            } else {
                                context.application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
                            }
                        }
                        .build()
                        .show()
                    return@launch // 成功时提前结束
                } else if (attempt < 9) {
                    // 不是最后一次尝试，等待3秒
                    delay(3000)
                } else {
                    loading?.dismiss()
                    FSAlertDialog.Builder(context)
                        .setContentView(R.layout.fs_layout_pay_failed)
                        .setPositive("重新购买") {
                            payDialog(context, mallIdFS, mallNameFS, priceFS, priceContentFS,
                                orderTimeFS, cpOrderIdFS) {

                            }
                        }.setOnDismissListener {
                            if(fsPayResult?.payType==FoxSdkPayEnum.FOX_COIN){
                                return@setOnDismissListener
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                context.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
                            } else {
                                context.application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
                            }
                        }
                        .setCancelable(false)
                        .setNegative(
                            "返回游戏",
                            textColor = context.resources.getColor(R.color.fs_primary),
                            theme = "outline"
                        )
                        .withClose()
                        .build()
                        .show()
                }
            }
        }
    }

    // 需要时可以取消轮询
    fun stopPolling() {
        pollingJob?.cancel()
    }


    private val activityLifecycleCallbacks = object :
        Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityDestroyed(p0: Activity) {

        }

        override fun onActivityPaused(p0: Activity) {

        }

        override fun onActivityResumed(p0: Activity) {
            Log.e("onActivityResumed", "2222222222222222")
            fsPayResult?.let {
                if (it.isCheckPay) {
                    startPollingPaymentResult(p0, it)
                }
            }
        }

        override fun onActivitySaveInstanceState(
            p0: Activity,
            p1: Bundle
        ) {

        }

        override fun onActivityStarted(p0: Activity) {

        }

        override fun onActivityStopped(p0: Activity) {

        }
    }
}