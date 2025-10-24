package com.sohuglobal.foxsdk.utils.pay

/**
 * @file ChildPayType
 * 文件说明：子支付类型 503 微信 502 支付宝
 *
 * @author 王金强
 * @date 2025/10/21 16:17
 */
enum class ChildPayType(var value: Int, var explain: String){
    WX_PAY(503,"微信支付"),
    AL_PAY(502,"支付宝支付"),
    ERROR(-1,"错误")
}