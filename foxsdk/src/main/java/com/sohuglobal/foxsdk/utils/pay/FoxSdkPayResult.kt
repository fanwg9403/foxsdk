package com.sohuglobal.foxsdk.utils.pay

import android.annotation.SuppressLint
import android.content.Context
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.core.WishFoxSdk
import com.sohuglobal.foxsdk.data.network.FoxSdkApiService
import com.sohuglobal.foxsdk.data.network.FoxSdkRetrofitManager
import com.sohuglobal.foxsdk.ui.view.widgets.FSLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @file FoxSdkPayResult
 * 文件说明：支付结果查询
 *
 * @author 王金强
 * @date 2025/10/21 17:52
 */
object FoxSdkPayResult {
    var loading: FSLoadingDialog? = null

    fun queryResult(context: Context, orderId:String,callback: (Boolean) -> Unit){
        var resultPay: Boolean = false
        CoroutineScope(Dispatchers.Main).launch {
            resultPay = payResult(context,orderId)
            callback(resultPay)
        }

    }
    @SuppressLint("SuspiciousIndentation")
    suspend fun payResult(context: Context, orderId:String): Boolean {
//        loading = FSLoadingDialog(context)
//        loading?.show()
        val service: FoxSdkApiService by lazy {
            FoxSdkRetrofitManager.getApiService()
        }
       var resultPay: Boolean = false
           withContext(Dispatchers.IO) {
            runCatching {
                val params = mapOf(
                    "order_id" to orderId,
                    "channel_id" to WishFoxSdk.getConfig().channelId,
                    "app_id" to WishFoxSdk.getConfig().appId
                )
                service.getOrderDetail(params)
            }.onSuccess {
//                loading?.dismiss()
                //0待支付1已支付2已发货-1支付失败
                resultPay =  when (it.data?.status) {
                    0-> false
                    1-> true
                    2-> true
                    -1-> false
                    else -> false
                }
            }.onFailure {
//                loading?.dismiss()
                Toaster.show(it.message)
                resultPay = false
            }
        }
        return resultPay
    }
}