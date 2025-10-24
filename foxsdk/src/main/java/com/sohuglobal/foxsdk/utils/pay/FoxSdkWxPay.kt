package com.sohuglobal.foxsdk.utils.pay

import android.content.Context
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.utils.FoxSdkConstant
import com.sohuglobal.foxsdk.utils.FoxSdkSPUtils
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * @file FoxSdkWxPay
 * 文件说明： 微信支付
 *
 * @author 王金强
 * @date 2025/10/21 14:35
 */
object FoxSdkWxPay {

    /**
     * 微信小程序支付通用方法
     * @param context 上下文
     * @param map 微信小程序支付参数，根据实际业务需求传入不同的参数，根据小程序跳转路径进行参数设置
     */
    fun wXMiniProgramPayment(context:Context,map: Map<String, Any>): Boolean{
        if (!FoxSdkWechatService.isWeChatInstalled) {
            Toaster.show(context.getString(R.string.fs_str_wx_pay))
            return false
        }
        val storage = FoxSdkSPUtils.getInstance()
        val api: IWXAPI = WXAPIFactory.createWXAPI(context, FoxSdkConfig.APP_ID)
        val req: WXLaunchMiniProgram.Req = WXLaunchMiniProgram.Req()
        req.userName = FoxSdkConfig.MINI_PROGRAM_ID // 填小程序原始id
        var path = "contentPackages/payPage/payPage?token=${storage.get(FoxSdkConstant.AUTHORIZATION)}"
        val toMutableMap = map.toMutableMap()
        if(toMutableMap["payChannel"]!=="Android"){
            toMutableMap["payChannel"] = "Android"
        }
        toMutableMap["childPayType"] = ChildPayType.WX_PAY.value
        toMutableMap.forEach {
            path += "&${it.key}=${if(it.value is  String) {
                if((it.value as String?).isNullOrEmpty()){
                    ""
                }else{
                    it.value
                }
            }else{
                it.value ?: ""
            }
            }"
        }

//        LogUtils.d("path:${path}")
        req.path = path
        //小程序版本  0-正式版； 1-测试版； 2-体验版。
//        if (CORE.ENVIRONMENT_CURRENT == ENVIRONMENT.PRODUCTION) {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
//        } else {
//            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
//        }
        api.sendReq(req)
        return true
    }
}