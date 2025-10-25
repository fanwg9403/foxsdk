package com.sohuglobal.foxsdk.data.network

import com.sohuglobal.foxsdk.data.model.FoxSdkBaseResponse
import com.sohuglobal.foxsdk.data.model.entity.FSCheckOrder
import com.sohuglobal.foxsdk.data.model.entity.FSCreateOrder
import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSGameRecord
import com.sohuglobal.foxsdk.data.model.entity.FSHomeBanner
import com.sohuglobal.foxsdk.data.model.entity.FSLoginResult
import com.sohuglobal.foxsdk.data.model.entity.FSMessage
import com.sohuglobal.foxsdk.data.model.entity.FSPageContainer
import com.sohuglobal.foxsdk.data.model.entity.FSRechargeRecord
import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile
import com.sohuglobal.foxsdk.data.model.entity.FSWinFoxCoin
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 *
 * 主要功能: Api接口
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 16:27
 */
interface FoxSdkApiService {

    /**
     * 获取用户信息
     */
    @GET("/app/user/profile")
    suspend fun getUserProfile(): FoxSdkBaseResponse<FSUserInfo>

    /**
     * 获取用户关注列表（分页）
     */
    @GET("users/{userId}/followings")
    suspend fun getUserFollowings(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): FoxSdkBaseResponse<List<FSUserInfo>>

    /**
     * 获取新手礼包列表（分页）
     */
    @JvmSuppressWildcards
//    @GET("/admin/mail_manage/mail_list")
    @GET("/api/mail/list")
    suspend fun getStarterPacks(
        @QueryMap params: Map<String, Any>
    ): FoxSdkBaseResponse<FSPageContainer<FSStarterPack>>

    /**
     * 领取礼包
     */
//    @POST("/admin/mail_manage/mail_update")
    @POST("/api/mail/mark_all_received")
    suspend fun receiveStarterPack(@QueryMap params: Map<String, String>): FoxSdkBaseResponse<Any>

    /**
     * 登录接口
     */
    @GET("/api/user/login")
    suspend fun login(@QueryMap params: Map<String, String>): FoxSdkBaseResponse<FSLoginResult>

    @GET("/api/user/user_info")
    suspend fun getUserInfo(@QueryMap body: Map<String, String>): FoxSdkBaseResponse<FSUserProfile>

    /**
     * 获取赢狐币列表（分页）
     */
    @GET("/api/java/busy_order_list")
    suspend fun getWinFoxCoins(
        @Query("page_num") page: Int,
        @Query("page_size") pageSize: Int
    ): FoxSdkBaseResponse<List<FSWinFoxCoin>>


    /**
     * 获取充值记录列表（分页）
     */
    @GET("/admin/game_data/recharge_list")
    suspend fun getRechargeRecords(
        @Query("page_num") page: Int,
        @Query("page_size") pageSize: Int
    ): FoxSdkBaseResponse<FSPageContainer<FSRechargeRecord>>

    @DELETE("/api/user/logout")
    suspend fun logout(): FoxSdkBaseResponse<Any>

    /**
     * 获取游戏记录列表（分页）
     */
    @GET("/api/user/game_play_log_list")
    suspend fun getGameRecordList(
        @Query("page_num") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("channel_id") channelId: String,
        @Query("app_id") appId: String
    ): FoxSdkBaseResponse<FSPageContainer<FSGameRecord>>

    /**
     * 系统消息（公告）列表
     */
    @GET("/api/mail/list")
    suspend fun getSystemMessages(
        @Query("page_num") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("channel_id") channelId: String,
        @Query("app_id") appId: String,
        @Query("type") type: Int
    ): FoxSdkBaseResponse<FSPageContainer<FSMessage>>

    @POST("/api/mail/mark_all_read")
    suspend fun read(
        @Body body: RequestBody
//        @Query("mail_id") id: Long,
//        @Query("channel_id") channelId: String,
//        @Query("app_id") appId: String
    ): FoxSdkBaseResponse<Any>

    @POST("/api/mail/mark_all_read")
    suspend fun read(
        @Query("channel_id") channelId: String,
        @Query("app_id") appId: String
    ): FoxSdkBaseResponse<Any>

    /**
     * 账户狐币余额
     */
    @POST("/api/java/user_virtual_info")
    suspend fun getUserVirtualInfo(): FoxSdkBaseResponse<FSCoinInfo>

    /**
     * 商品下单
     */
    @JvmSuppressWildcards
    @POST("/api/user/order")
    suspend fun createOrder(@QueryMap params: Map<String, Any>): FoxSdkBaseResponse<FSCreateOrder>

    /**
     * 订单查询接口
     *   GET /api/user/order_detail
     *   接口ID：281821525
     *   接口地址：https://app.apifox.com/link/project/5746419/apis/api-281821525
     */
    @JvmSuppressWildcards
    @GET("/api/user/order_detail")
    suspend fun getOrderDetail(@QueryMap params: Map<String, Any>): FoxSdkBaseResponse<FSCheckOrder>

    @GET("/api/java/ad_list")
    suspend fun getAdvertiseList(
        @Query("page_num") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("ad_place_code") adPlace: String
    ): FoxSdkBaseResponse<List<FSHomeBanner>>

    @POST("/api/user/code")
    suspend fun sendSmsCode(@QueryMap params: Map<String, String>): FoxSdkBaseResponse<Object>
}