package com.sohuglobal.foxsdk.utils.customerservice

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.qiyukf.nim.highavailable.LogUtils
import com.qiyukf.nimlib.sdk.NimIntent
import com.qiyukf.nimlib.sdk.RequestCallback
import com.qiyukf.nimlib.sdk.StatusBarNotificationConfig
import com.qiyukf.nimlib.sdk.msg.constant.NotificationExtraTypeEnum
import com.qiyukf.nimlib.sdk.msg.model.IMMessage
import com.qiyukf.unicorn.api.ConsultSource
import com.qiyukf.unicorn.api.OnBotEventListener
import com.qiyukf.unicorn.api.UICustomization
import com.qiyukf.unicorn.api.Unicorn
import com.qiyukf.unicorn.api.UnreadCountChangeListener
import com.qiyukf.unicorn.api.YSFOptions
import com.qiyukf.unicorn.api.YSFUserInfo
import com.qiyukf.unicorn.api.customization.input.ActionPanelOptions
import com.qiyukf.unicorn.api.customization.input.InputPanelOptions
import com.qiyukf.unicorn.api.event.EventProcessFactory
import com.qiyukf.unicorn.api.event.SDKEvents
import com.qiyukf.unicorn.api.pop.POPManager
import com.qiyukf.unicorn.api.pop.Session
import com.sohuglobal.foxsdk.R
import androidx.core.net.toUri
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile


/**
 * 七鱼客服
 */
class QiyukfHelper {


     val key = "296904123fc762ad332610839363736a"

     var tempId: Long = 6676107

    companion object {


        val instance by lazy { SingletonHolder.INSTANCE }

        private object SingletonHolder {
            val INSTANCE = QiyukfHelper()
        }

    }


    fun init(context: Context, clazz: Class<out AppCompatActivity>) {

        val options = YSFOptions()

        /**
         * UI样式使用上方链接->访客端 中配置的模板
         * @Link https://wkhq.qiyukf.com/madmin/session/leave
         */
        options.templateId = tempId

        options.statusBarNotificationConfig = StatusBarNotificationConfig()
        options.statusBarNotificationConfig.notificationSmallIconId = R.mipmap.ic_kf_icon
        options.statusBarNotificationConfig.notificationEntrance = clazz
        options.statusBarNotificationConfig.notificationExtraType = NotificationExtraTypeEnum.MESSAGE
        options.onBotEventListener = object : OnBotEventListener() {
            override fun onUrlClick(context: Context, url: String): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                return true
            }
        }

        options.sdkEvents = SDKEvents()
        options.sdkEvents.eventProcessFactory = EventProcessFactory { eventType ->
            if (eventType == 5) QiyukfRequestPermissionEvent(context) else null
        }

        options.uiCustomization = UICustomization()
//        options.uiCustomization.titleBackgroundColor = Color.parseColor("#0E1827")//设置标题栏颜色
//        options.uiCustomization.statusBarColor = Color.parseColor("#0E1827")//设置状态栏颜色
//        options.uiCustomization.titleBarStyle = 1
        options.uiCustomization.leftAvatar = getDrawablePath(context, R.mipmap.ic_kf_icon)
        options.uiCustomization.rightAvatar = FSUserProfile.getInstance()?.userInfo?.avatar
        Unicorn.config(context, key, options, GlideImageLoader(context))
    }

    fun getDrawablePath(context: Context, resourceId: Int): String {
        val uri = (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.resources.getResourcePackageName(resourceId) + '/' +
                context.resources.getResourceTypeName(resourceId) + '/' +
                context.resources.getResourceEntryName(resourceId)).toUri()
        return uri.toString()
    }
    fun initKFSDK() {
        Unicorn.initSdk()
    }

    fun openCustomerService(context: Context, title: String, sourceUrl: String, sourceTitle: String, otherInfo: String) {
        /**
         * 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入。
         * 三个参数分别为：来源页面的url，来源页面标题，来源页面额外信息（保留字段，暂时无用）。
         * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
         */
        /**
         * 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入。
         * 三个参数分别为：来源页面的url，来源页面标题，来源页面额外信息（保留字段，暂时无用）。
         * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
         */
        val source = ConsultSource(sourceUrl, sourceTitle, otherInfo)

        /**
         * 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable()，
         * 如果返回为false，该接口不会有任何动作
         *
         * @param context 上下文
         * @param title   聊天窗口的标题
         * @param source  咨询的发起来源，包括发起咨询的url，title，描述信息等
         */
        Unicorn.openServiceActivity(context, title, source)
    }


    /**
     * 设置用户信息
     * 注意，该接口调用的时机应该是在接入方 App 登录的时候，不应该在进入客服界面之前去调用该方法
     * 注意：目前不支持跨设备的消息漫游，在其他设备上通过 setUserInfo(YSFUserInfo) 设置相同的用户， 这个设备上不能收到留言。
     */
    fun setUserInfo(user: YSFUserInfoBean,callback: RequestCallback<Void>? = null) {
        val userInfo = YSFUserInfo();
        // App 的用户 ID
        userInfo.userId = user.userId//"uid";
        // 当且仅当开发者在管理后台开启了 authToken 校验功能时，该字段才有效
        userInfo.authToken = user.authToken;//"auth-token-from-user-server";
        // CRM 扩展字段
        userInfo.data = "[\n" +
                "        {\"key\":\"real_name\", \"value\":\"${user.realName}\"},\n" +
                "        {\"key\":\"mobile_phone\", \"hidden\":true, \"value\":\"${user.mobilePhone}\"},\n" +
                "        {\"key\":\"email\", \"value\":\"${user.email}\"},\n" +
                "        {\"key\":\"avatar\", \"value\": \"${user.avatar}\"},\n" +
                "        {\"index\":0, \"key\":\"account\", \"label\":\"账号\", \"value\":\"${user.account}\" , \"href\":\"http://example.domain/user/zhangsan\"},\n" +
                "        {\"index\":1, \"key\":\"sex\", \"label\":\"性别\", \"value\":\"${user.sex}\"},\n" +
                "        {\"index\":5, \"key\":\"reg_date\", \"label\":\"注册日期\", \"value\":\"${user.regDate}\"},\n" +
                "        {\"index\":6, \"key\":\"last_login\", \"label\":\"上次登录时间\", \"value\":\"${user.lastLogin}\"}\n" +
                "        ]"
        Unicorn.setUserInfo(userInfo,callback)
    }

    /**
     * 如果一定要在进入客服之前调用 setUserInfo 方法，那么需要使用带有 callback 的 setUserInfo 接口，并在 onSuccess
     * 中进入客服界面，例如：
     * 注意：目前不支持跨设备的消息漫游，在其他设备上通过 setUserInfo(YSFUserInfo) 设置相同的用户， 这个设备上不能收到留言。
     */
    fun setUserInfoAndOpenCustomerService(context: Context, user: YSFUserInfoBean) {
        val userInfo = YSFUserInfo();
        // App 的用户 ID
        userInfo.userId = user.userId//"uid";
        // 当且仅当开发者在管理后台开启了 authToken 校验功能时，该字段才有效
        userInfo.authToken = user.authToken//"auth-token-from-user-server";
        // CRM 扩展字段
        userInfo.data = "[\n" +
                "        {\"key\":\"real_name\", \"value\":\"${user.realName}\"},\n" +
                "        {\"key\":\"mobile_phone\", \"hidden\":true, \"value\":\"${user.mobilePhone}\"},\n" +
                "        {\"key\":\"email\", \"value\":\"${user.email}\"},\n" +
                "        {\"key\":\"avatar\", \"value\": \"${user.avatar}\"},\n" +
                "        {\"index\":0, \"key\":\"account\", \"label\":\"账号\", \"value\":\"${user.account}\" , \"href\":\" \"},\n" +
                "        {\"index\":1, \"key\":\"sex\", \"label\":\"性别\", \"value\":\"${user.sex}\"},\n" +
                "        {\"index\":5, \"key\":\"reg_date\", \"label\":\"注册日期\", \"value\":\"${user.regDate}\"},\n" +
                "        {\"index\":6, \"key\":\"last_login\", \"label\":\"上次登录时间\", \"value\":\"${user.lastLogin}\"}\n" +
                "        ]"

        Unicorn.setUserInfo(userInfo, object : RequestCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Unicorn.openServiceActivity(context, "在线客服", null)
            }

            override fun onFailed(errorCode: Int) {
                /**
                 * 310	登录IP或MAC被禁
                 * 315	内部帐户不允许在该地址登陆
                 * 403	用户被封禁
                 * 408	操作超时
                 * 414	参数错误
                 * 415	网络连接出现问题
                 * 701	设置 UserInfo id 为 null
                 * 702	没有退出上一个 user 就直接调用了 setUserInfo
                 * 703	使用融合 SDK 没有先登录云信
                 * 1001	appkey不存在
                 * 1002	deviceId错误
                 * 1003	其他错误
                 * 1004	authToken校验错误
                 */
                LogUtils.e("TAG====",errorCode.toString())
            }

            override fun onException(throwable: Throwable) {
                LogUtils.e("TAG====",throwable.message)
            }
        })

    }

    /**
     * 设置对话UI（七鱼客服）
     *
     * @return
     */
    private fun uiCustomization(context: Context): UICustomization {
        // 以下示例的图片均无版权，请勿使用
        val customization = UICustomization()
        //标题栏背景颜色
        customization.titleBackgroundResId = R.color.white
        //标题栏风格，影响标题和标题栏上按钮的颜色(0浅色系，1深色系)
        customization.titleBarStyle = 1
        //键盘控制
        customization.hideKeyboardOnEnterConsult = true
        //输入框内字体颜色（一定要设置这个属性，有的手机上不设置此属性会出现输入的字体色是透明的）
        customization.inputTextColor = R.color.color_333333
        //访问者头像
        customization.rightAvatar = ""
        //客服头像
        // customization.leftAvatar = url;
        return customization
    }


    /**
     * 自定义输入栏 区域
     */
    private fun initInputOptions(options: YSFOptions) {
        options.inputPanelOptions = InputPanelOptions()
        options.inputPanelOptions.showActionPanel = true
        options.inputPanelOptions.moreIconResId = R.mipmap.stat_notify_more
//        options.inputPanelOptions.voiceIconResId = R.drawable.ic_launcher
//        options.inputPanelOptions.emojiIconResId = R.drawable.ic_launcher
        options.inputPanelOptions.actionPanelOptions = ActionPanelOptions()
        //实现 getActionList 方法，返回输入更多弹框的列表，SDK 中默认提供了 AlbumAction(相册)、CameraAction(相机)等 Action
//        options.inputPanelOptions.actionPanelOptions.actionListProvider = ActionListProvider {
//            val list: MutableList<BaseAction> = ArrayList()
//            list.add(ImageAction(R.drawable.ic_menu_report_image, "图片"))
//            list.add(TextAction(R.drawable.ic_kaola, R.string.ysf_action_text))
//            list.add(SendProductAction(R.drawable.ic_launcher, R.string.ysf_action_product))
//            list.add(CameraAction(R.drawable.ic_menu_camera, "拍照"))
//            list.add(AlbumAction(R.drawable.ic_menu_camera, "相册"))
//            list
//        }
        //options.inputPanelOptions.actionPanelOptions.backgroundColor = -0xffff01 //自定义输入更多弹框背景颜色
    }


    fun isStatusBarNotificationClick(context: Activity): Boolean {
        //如果是从状态栏消息模块点过来的直接进入
        val csintent: Intent = context.intent
        if (csintent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            // 打开客服窗口
            openCustomerService(context, "在线客服", "Main", "", "")
            // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
            context.intent = Intent()
            context.finish()
            return true
        }
        return false
    }

    /**
     * 客服未读消息数变化监听
     *
     * @param listener
     */
    fun addUnreadCountChangeListener(listener: UnreadCountChangeListener?) {
        Unicorn.addUnreadCountChangeListener(listener, true)
    }

    /**
     * 获取最后一条消息
     */
    fun queryLastMessage(): IMMessage {
        return Unicorn.queryLastMessage()
    }

    /**
     * 获取客服与商家消息列表
     */
    fun getSessionList(): List<Session> {
        return POPManager.getSessionList()
    }

    /**
     * 如前所述，当关联的用户从 App 注销后，也应当调用 SDK 的注销接口 Unicorn.logout()。
     * 这样，注销之后，客服再给前面用户发送消息，将进入留言，等到该用户下次再在同一台设备上登录后，能够再看到。
     * 如果 App 的用户注销后，不调用七鱼的 logout 接口，
     * 七鱼不知道用户已经变更，那么客服如果给前面一个用户发起会话，发送消息，当前设备将仍旧能够收到消息，造成混乱。
     *
     * Unicorn.logout() 接口等效于 Unicorn.setUserInfo(null)。
     *
     * 注意：目前不支持跨设备的消息漫游，在其他设备上通过 setUserInfo(YSFUserInfo) 设置相同的用户， 这个设备上不能收到留言。
     */
    fun logout() {
        Unicorn.logout()
    }

    /**
     * 清除文件缓存，将删除SDK接收过的所有文件。<br>
     * 建议在工作线程中执行该操作。
     */
    fun clearCache() {
        Unicorn.clearCache()
    }


    class YSFUserInfoBean {
        var userId: String = ""
        var authToken: String = ""
        var realName: String = ""
        var mobilePhone: String = ""
        var email: String = ""
        var avatar: String = ""
        var account: String = ""
        var sex: String = ""
        var regDate: String = ""
        var lastLogin: String = ""
    }

}