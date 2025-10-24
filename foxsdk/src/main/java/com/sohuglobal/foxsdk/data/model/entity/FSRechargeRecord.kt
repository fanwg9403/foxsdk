package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @file FSRechargeRecord
 * 文件说明：充值记录实体
 *
 * @author 王金强
 * @date 2025/10/16 18:25
 */
data class FSRechargeRecord(
    /**
     * 游戏ID
     */
    @SerializedName("app_id")
    val appID: Long? = null,

    /**
     * 游戏名称
     */
    @SerializedName("app_name")
    val appName: String? = null,

    /**
     * 渠道ID
     */
    @SerializedName("channel_id")
    val channelID: Long? = null,

    /**
     * 第三方平台订单ID
     */
    @SerializedName("cp_order_id")
    val cpOrderID: String? = null,

    /**
     * 订单创建时间
     */
    @SerializedName("create_time")
    val createTime: String? = null,

    /**
     * 货币单位（CNY）
     */
    val currency: String? = null,

    /**
     * 回调次数
     */
    @SerializedName("handle_num")
    val handleNum: Int? = null,

    /**
     * 狐少少昵称
     */
    @SerializedName("hss_nick_name")
    val hssNickName: String? = null,

    /**
     * 狐少少ID
     */
    @SerializedName("hss_user_id")
    val hssUserID: String? = null,

    val id: Long? = null,

    /**
     * 商品名称
     */
    @SerializedName("mall_name")
    val mallName: String? = null,

    /**
     * 商品SKU
     */
    @SerializedName("mall_sku")
    val mallSku: String? = null,

    /**
     * 狐少少订单ID
     */
    @SerializedName("order_id")
    val orderID: String? = null,

    /**
     * 支付类型 1支付宝 2微信 3微信app支付
     */
    @SerializedName("pay_type")
    val payType: Int? = null,

    /**
     * 手机号
     */
    val phone: String? = null,

    /**
     * 订单价格（单位分）
     */
    val price: Double? = null,

    /**
     * 角色ID
     */
    @SerializedName("role_id")
    val roleID: String? = null,

    /**
     * 角色名称
     */
    @SerializedName("role_name")
    val roleName: String? = null,

    /**
     * 服务器ID
     */
    @SerializedName("server_id")
    val serverID: Long? = null,

    /**
     * 服务器名称
     */
    @SerializedName("server_name")
    val serverName: String? = null,

    /**
     * 支付状态-1取消支付 0未支付 1已支付 2已发货
     */
    val status: Int? = null,

    /**
     * 是否测试订单
     */
    @SerializedName("test_status")
    val testStatus: Int? = null,

    /**
     * 订单修改时间
     */
    @SerializedName("updated_time")
    val updatedTime: String? = null,

    /**
     * 用户编号
     */
    @SerializedName("user_id")
    val userID: Long? = null
)
