package com.sohuglobal.foxsdk.ui.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.model.entity.FSPayResult
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager
import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.databinding.FsDialogPayBinding
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import com.sohuglobal.foxsdk.utils.FoxSdkPayEnum
import com.sohuglobal.foxsdk.utils.onClick
import com.sohuglobal.foxsdk.utils.pay.FoxSdkAliPay
import com.sohuglobal.foxsdk.utils.pay.FoxSdkWechatService
import com.sohuglobal.foxsdk.utils.pay.FoxSdkWxPay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 16点 23 分
 * @Desc TODO:
 */
class FSPayDialog(val ctx: Context) : Dialog(ctx, R.style.FSLoadingDialog) {

    private val binding: FsDialogPayBinding by lazy { FsDialogPayBinding.inflate(layoutInflater) }

    private var loading: FSLoadingDialog? = null

    /** channel_id 渠道ID
     * app_id 应用ID*/
    var channelId: String = WishFoxSdk.getConfig().channelId
    var appId: String = WishFoxSdk.getConfig().appId
    var mallId: String = ""
    var mallName: String = ""
    var price: String = ""
    var orderTime: Long = 0
    var cpOrderId: String = ""

    /**
     * 设置支付下单参数
     * mall_id  商品ID
     * mall_name 商品名称
     * price 金额
     * order_time 下单时间
     * cp_order_id 游戏方订单ID
     */
    fun setPayParams(
        mallId: String,
        mallName: String,
        price: String,
        orderTime: Long,
        cpOrderId: String
    ) = apply {
        this.mallId = mallId
        this.mallName = mallName
        this.price = price
        this.orderTime = orderTime
        this.cpOrderId = cpOrderId

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fsCtvAgree.onClick {
            binding.fsCtvAgree.isChecked = !binding.fsCtvAgree.isChecked
            binding.fsTvConfirm.isEnabled = binding.fsCtvAgree.isChecked
            binding.fsTvConfirm.alpha = if (binding.fsCtvAgree.isChecked) 1f else 0.5f
        }
        binding.fsCheckRadioFoxCoin.setText(ctx.getString(R.string.fs_fox_coin_pay) + "（余额：${FSCoinInfo.getInstance()?.foxCoin ?: 0}）")
        binding.fsTvConfirm.onClick {
            if (!binding.fsCtvAgree.isChecked) {
                Toaster.show("请先阅读并同意${ctx.getString(R.string.fs_pay_agreement)}协议")
                return@onClick
            }
            loading = FSLoadingDialog(ctx)
            loading?.show()
            when (binding.fsCheckRadioGroup.getCheckedItemPosition()) {
                0 -> {
                    // 狐币
                    CoroutineScope(Dispatchers.Main).launch {
                        pay(FoxSdkPayEnum.FOX_COIN)
                    }
                    dismiss()
                }

                1 -> {
                    // 支付宝
                    CoroutineScope(Dispatchers.Main).launch {
                        pay(FoxSdkPayEnum.ALI_PAY)
                    }
                    dismiss()
                }

                2 -> {
                    // 微信
                    CoroutineScope(Dispatchers.Main).launch {
                        pay(FoxSdkPayEnum.WECHAT)
                    }
                    dismiss()
                }

                else -> {
                    Toaster.show("请选择支付方式")
                }
            }
        }
    }

    //调用支付下单
    suspend fun pay(payType: FoxSdkPayEnum) {
        val service: FoxSdkApiService by lazy {
            FoxSdkRetrofitManager.getApiService()
        }
        withContext(Dispatchers.IO) {
            runCatching {
                val params = hashMapOf<String, Any>()
                params["channel_id"] = channelId
                params["app_id"] = appId
                params["mall_id"] = mallId
                params["mall_name"] = mallName
                params["price"] = price
                params["order_time"] = orderTime
                params["cp_order_id"] = cpOrderId
                params["pay_type"] = when (payType) {
                    FoxSdkPayEnum.FOX_COIN -> 6
                    FoxSdkPayEnum.ALI_PAY -> 1
                    FoxSdkPayEnum.WECHAT -> 3
                }
                service.createOrder(params)
            }.onSuccess {
                if (it.code == 200) {
                    when (payType) {
                        FoxSdkPayEnum.FOX_COIN -> {
                            // 狐币
                            loading?.dismiss()
                            onPayCreate?.invoke(FSPayResult(true, it.data?.trade_number ?: (it.data?.busy_code ?: ""),
                                FoxSdkPayEnum.FOX_COIN))
                        }

                        FoxSdkPayEnum.ALI_PAY -> {
                            // 支付宝
                            it.data?.code_url?.let { codeUrl ->
                                if(FoxSdkAliPay.payAliYiMa(ctx, codeUrl, it.data.jump_url)){
                                    onPayCreate?.invoke(FSPayResult(true,it.data.pos_seq,
                                        FoxSdkPayEnum.ALI_PAY))
                                }
                            }?: run {
                                Toaster.show("支付失败")
                            }
                            loading?.dismiss()
                        }

                        FoxSdkPayEnum.WECHAT -> {
                            // 微信
                            //初始化微信支付
                            FoxSdkWechatService.init(context)
                            val params = hashMapOf<String, Any>()
                            params["app_id"] = appId
                            params["mall_id"] = mallId
                            params["mall_name"] = mallName
                            params["price"] = price
                            params["pay_type"] = 1
                            params["channel_id"] = channelId
                            params["cp_order_id"] = cpOrderId
                            params["paySource"] = 23
                            if(FoxSdkWxPay.wXMiniProgramPayment(ctx, params)){
                                onPayCreate?.invoke(FSPayResult(true,it.data?.pos_seq ?:"",
                                    FoxSdkPayEnum.WECHAT))
                            }
                            loading?.dismiss()
                        }
                    }
                } else {
                    loading?.dismiss()
                    Toaster.show(it.message)
                }
            }.onFailure {
                loading?.dismiss()
                Toaster.show(it.message)
            }
        }
    }

    private var onConfirm: ((FoxSdkPayEnum) -> Unit)? = null
    fun setOnConfirm(listener: (FoxSdkPayEnum) -> Unit) = apply {
        onConfirm = listener
    }

    //是否须要调用检测创建订单结果
    private var onPayCreate: ((FSPayResult) -> Unit)? = null
    fun setOnPayCreate(listener: (FSPayResult) -> Unit) = apply {
        onPayCreate = listener
    }

    fun setPayType(payType: FoxSdkPayEnum) = apply {
        binding.fsCheckRadioGroup.setCheckedItem(
            when (payType) {
                FoxSdkPayEnum.FOX_COIN -> 0
                FoxSdkPayEnum.ALI_PAY -> 1
                FoxSdkPayEnum.WECHAT -> 2
            }
        )
    }

    override fun show() {
        if (mallId.isEmpty() || mallName.isEmpty() ||
            price.isEmpty() || orderTime == 0L || cpOrderId.isEmpty()
        ) {
            Toaster.show("请检查参数")
            return
        }
        super.show()
    }

    fun setPayName(text: String) = apply {
        binding.fsTvName.text = text
    }

    fun setPayName(@StringRes text: Int) = apply {
        binding.fsTvName.text = ctx.getString(text)
    }

    fun setPayInfo(text: String) = apply {
        binding.fsTvInfo.text = text
    }

    fun setPayInfo(@StringRes text: Int) = apply {
        binding.fsTvInfo.text = ctx.getString(text)
    }

    fun setDisableFoxCoinPay(disable: Boolean) = apply {
        binding.fsCheckRadioFoxCoin.isEnabled = !disable
    }
}