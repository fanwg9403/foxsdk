package com.sohuglobal.foxsdk.utils.customerservice

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.qiyukf.nim.highavailable.LogUtils
import com.qiyukf.unicorn.api.event.EventCallback
import com.qiyukf.unicorn.api.event.UnicornEventBase
import com.qiyukf.unicorn.api.event.entry.RequestPermissionEventEntry
import com.sohuglobal.foxsdk.ui.view.dialog.FSPermissionMissTipsDialog
import com.sohuglobal.foxsdk.ui.view.dialog.FSPrivacyFloatingTipsDialog


/**
 * @Author zs
 * @CreateTime 2024年06月13日 11:54
 * @Desc https://qiyukf.com/docs/guide/android/7-%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BA%8B%E4%BB%B6.html#sdk-%E7%94%B3%E8%AF%B7%E6%9D%83%E9%99%90%E4%BA%8B%E4%BB%B6%E7%9B%91%E5%90%AC
 */
class QiyukfRequestPermissionEvent(private val mApplicationContext: Context?) :
    UnicornEventBase<RequestPermissionEventEntry?> {
    private val h5MessageHandlerMap: MutableMap<String?, String?> = HashMap<String?, String?>()


    init {
        h5MessageHandlerMap.put("android.permission.RECORD_AUDIO", "麦克风")
        h5MessageHandlerMap.put("android.permission.CAMERA", "相机")
        h5MessageHandlerMap.put("android.permission.READ_EXTERNAL_STORAGE", "存储")
        h5MessageHandlerMap.put("android.permission.WRITE_EXTERNAL_STORAGE", "存储")
        h5MessageHandlerMap.put("android.permission.READ_MEDIA_AUDIO", "多媒体文件")
        h5MessageHandlerMap.put("android.permission.READ_MEDIA_IMAGES", "多媒体文件")
        h5MessageHandlerMap.put("android.permission.READ_MEDIA_VIDEO", "多媒体文件")
        h5MessageHandlerMap.put("android.permission.POST_NOTIFICATIONS", "通知栏权限")
    }

    private fun transToPermissionStr(permissionList: MutableList<String?>?): String {
        if (permissionList == null || permissionList.size == 0) {
            return ""
        }
        val set = HashSet<String?>()
        for (i in permissionList.indices) {
            if (!TextUtils.isEmpty(h5MessageHandlerMap.get(permissionList.get(i)))) {
                set.add(h5MessageHandlerMap.get(permissionList.get(i)))
            }
        }
        if (set.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for (temp in set) {
            str.append(temp)
            str.append("、")
        }
        if (str.length > 0) {
            str.deleteCharAt(str.length - 1)
        }
        return str.toString()
    }

    /**
     * 该方法为点击相应的权限场景,用户可以通过RequestPermissionEventEntry.getPermissionList()拿到相应的权限,根据
     * 自己APP的权限规则,作自己的处理.
     *
     *
     * 比如判断客户之前点击的是拒绝权限还是不再询问,可以使用 AppCompatActivity.shouldShowRequestPermissionRationale()
     * 方法等各种情况都是在这个回调中进行自己的处理.
     *
     *
     * 各种情况都处理完了,可以告诉SDK,是要申请权限还是拒绝,调用SDK相应的方法.
     * callback.onProcessEventSuccess(requestPermissionEventEntry):用户同意授予权限
     * callback.onInterceptEvent():用户不授予权限，SDK自己处理不授予权限的提醒;或者用户自己处理不授予权限的提醒,就不要调用这个方法了
     *
     * @param requestPermissionEventEntry 获取权限相关的类
     * @param context                     当前界面的 context 对象，使用之前请判断是否为 null
     * @param callback                    sdk 的回调对象  注意：如果该事件 sdk 不需要回调的时候，这个对象会为 null，所以当使用的时候需要做一下非null判断
     */

    override fun onEvent(
        requestPermissionEventEntry: RequestPermissionEventEntry?,
        context: Context?,
        callback: EventCallback<RequestPermissionEventEntry?>?
    ) {
        //申请权限的场景
        //从本地选择媒体文件(视频和图片):0
        //拍摄视频场景:1
        //保存图片到本地:2
        //保存视频到本地:3
        //选择本地视频:4
        //选择本地文件:5
        //选择本地图片:6
        //拍照:7
        //录音:8
        //视频客服:9
        //通知栏权限:10

        val type = requestPermissionEventEntry?.getScenesType()

        if (type == 10) {
            //Toast.makeText(mApplicationContext, "适配Android13,没有通知栏权限,需要给通知栏权限", Toast.LENGTH_SHORT).show();
            return
        } else if (type == 5) {
            val list: MutableList<String?> = ArrayList<String?>()
            list.add(getImagePermission())
            list.add(getVideoPermission())
            list.add(getMediaAudioPermission())
            requestPermissionEventEntry.setPermissionList(list)
        }



        requestPermissionListBys(
            context as Activity?,
            requestPermissionEventEntry?.getPermissionList(),
            getTypeName(type),
            object : PermissionCallbackBys {
                override fun onPermissionComplete(isGranted: Boolean) {
                    if (isGranted) {
                        //如果想用户授予权限，需要调用 onProcessEventSuccess 告诉 SDK 处理成功
                        callback?.onProcessEventSuccess(requestPermissionEventEntry)
                    } else {
                        //用户不授予权限，告诉 SDK 用户没有授予调用onInterceptEvent,SDK自己处理不授予权限的提醒
                        //或者用户自己处理不授予权限的提醒,就不要调用这个方法了
                        //callback.onInterceptEvent();
                    }
                }

            })

        //        AlertDialog dialog = new AlertDialog.Builder(context).setMessage("为保证您" + type + "功能的正常使用，" + "需要使用您的：" + (TextUtils.isEmpty(permissionName) ? "相关" : permissionName) + "权限，\n" + "拒绝或取消不影响使用其他服务")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //如果想用户授予权限，需要调用 onProcessEventSuccess 告诉 SDK 处理成功
//                        callback.onProcessEventSuccess(requestPermissionEventEntry);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //用户不授予权限，告诉 SDK 用户没有授予调用onInterceptEvent,SDK自己处理不授予权限的提醒
//                        //或者用户自己处理不授予权限的提醒,就不要调用这个方法了
//                        callback.onInterceptEvent();
//                    }
//                }).create();
//        dialog.show();
    }


    /**
     * 当相关权限被拒绝后,客户自己的处理
     *
     * @return 返回值默认为false，若返回值为true，则客户自己处理
     */

    override fun onDenyEvent(p0: Context?, p1: RequestPermissionEventEntry?): Boolean {
        val permissionName = transToPermissionStr(p1?.getPermissionList())
        //        AlertDialog dialog = new AlertDialog.Builder(context).setMessage("您没有：" + (TextUtils.isEmpty(permissionName) ? "相关" : permissionName) + "权限，\n" + "是否进行其他设置")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //用户自定义拒绝后的权限处理
//                        Toast.makeText(context, "我自己处理没有的权限情况", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "我什么也没有做", Toast.LENGTH_SHORT).show();
//                    }
//                }).create();
//        dialog.show();
        return true
    }


    private fun getTypeName(type: Int?): String {
        when (type) {
            0 -> return "从本地选择媒体文件(视频和图片)"
            1 -> return "拍摄视频场景"
            2 -> return "保存图片到本地"
            3 -> return "保存视频到本地"
            4 -> return "选择本地视频"
            5 -> return "选择本地文件"
            6 -> return "选择本地图片"
            7 -> return "拍照"
            8 -> return "录音"
            9 -> return "视频客服"
            10 -> return "通知栏权限"
        }
        return ""
    }

    private fun getMediaAudioPermission(): String {
        val imageStr: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imageStr = Manifest.permission.READ_MEDIA_AUDIO
        } else {
            imageStr = Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return imageStr
    }

    private fun getVideoPermission(): String {
        val imageStr: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imageStr = Manifest.permission.READ_MEDIA_VIDEO
        } else {
            imageStr = Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return imageStr
    }

    private fun getImagePermission(): String {
        val imageStr: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imageStr = Manifest.permission.READ_MEDIA_IMAGES
        } else {
            imageStr = Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return imageStr
    }


    /**
     * 获取权限
     */
    var mPopupView: BasePopupView? = null
    fun requestPermissionListBys(
        activity: Activity?,
        permission: List<String?>?,
        typeName: String,
        permissionCallback: PermissionCallbackBys
    ) {
        val mPermission: MutableList<String?> = ArrayList<String?>()



        permission?.let { permissions ->
            activity?.let { activitys ->
                if (XXPermissions.isGranted(
                        activity,
                        permission
                    )) {
                    permissionCallback.onPermissionComplete(true)
                    return
                }

                for (s in permission) {
                    if (!XXPermissions.isGranted(activity, s)) {
                        s?.let { mPermission.add(it) }
                    }
                }
            }


        }

        activity?.let {
            val dialog = FSPrivacyFloatingTipsDialog(activity, mPermission as List<String>)

            mPopupView =
                XPopup.Builder(activity)
                    .isViewMode(true)
                    .asCustom(dialog)
                    .show()

            dialog.setMListener({ view ->
                XXPermissions.with(activity)
                    .permission(mPermission)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String?>,
                            allGranted: Boolean
                        ) {
                            if (!allGranted) {
                                //获取部分权限成功，但部分权限未正常授予
                                permissionCallback.onPermissionComplete(false)
                                showDialog(
                                    activity,
                                    mPermission,
                                    typeName,
                                    transToPermissionStr(permission as MutableList<String?>?)
                                )
                                return
                            }
                            permissionCallback.onPermissionComplete(allGranted)
                        }

                        override fun onDenied(
                            permissions: MutableList<String?>,
                            doNotAskAgain: Boolean
                        ) {
                            LogUtils.d("TAG====","doNotAskAgain--" + doNotAskAgain)

                            permissionCallback.onPermissionComplete(false)

                            showDialog(
                                activity,
                                mPermission,
                                typeName,
                                transToPermissionStr(permission as MutableList<String?>?)
                            )
                        }
                    })
            })
        }

    }

    interface PermissionCallbackBys {
        fun onPermissionComplete(isGranted: Boolean)
    }

    //打开提示dialog
    fun showDialog(
        activity: Context,
        permissions: MutableList<String?>,
        typeName: String,
        permissionName: String
    ) {
        var content = ""
        if (TextUtils.isEmpty(permissionName)) {
            content = "您已拒绝了 " + typeName + " 功能相关权限,如需使用,请手动授予权限"
        } else {
            content =
                "您已拒绝了 " + permissionName + " 相关权限,如需使用 " + typeName + " 功能" + ",请手动授予权限"
        }

        val dialog: FSPermissionMissTipsDialog = FSPermissionMissTipsDialog(activity, content)
        dialog.setMListener({ view ->
            XXPermissions.startPermissionActivity(activity, permissions)
        })

        XPopup.Builder(activity) //                .asConfirm("温馨提示", content, "取消", "去设置",
            //                        () -> XXPermissions.startPermissionActivity(activity, permissions), null, false)
            .isViewMode(true)
            .asCustom(dialog)
            .show()
    }
}
