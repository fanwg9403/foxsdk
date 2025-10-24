package com.sohuglobal.foxsdk.ui.view.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.lxj.xpopup.core.CenterPopupView
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.databinding.FsItemPrivacyfloatingtipsBinding

/**
 * 权限弹框
 */
@SuppressLint("ViewConstructor")
class FSPrivacyFloatingTipsDialog(context: Context, private var permissionList: List<String>) : CenterPopupView(context) {

    var listener: OnClickListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.fs_dialog_privacyfloatingtips
    }

    //设置监听
    fun setMListener(listener: OnClickListener) {
        this.listener = listener
    }

    private val mPermissionTipsAdapter by lazy { PermissionTipsAdapter() }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        val fsRv = findViewById<RecyclerView>(R.id.fs_rv)
        val fsOk = findViewById<TextView>(R.id.fs_tv_ok)
        val fsCancel = findViewById<TextView>(R.id.fs_tv_cancel)

        val itemData = arrayListOf<PermissionTips>()

        for (permission in permissionList) {
            val tips = PermissionTips()
            when (permission) {

                "Camera_And_Stored_Media_Files" -> {//相机和存储空间中的图片以及媒体文件
                    tips.title = activity.resources.getString(R.string.fs_camera_and_gallery_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_camera_and_gallery_permission_text)
                }


                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    tips.title = activity.resources.getString(R.string.fs_gallery_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_gallery_permission_text)
                }

                Manifest.permission.RECORD_AUDIO -> {
                    tips.title = activity.resources.getString(R.string.fs_microphone_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_microphone_permission_text)
                }

                Manifest.permission.CAMERA -> {
                    tips.title = activity.resources.getString(R.string.fs_camera_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_camera_permission_text)
                }


                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO -> {
                    tips.title = activity.resources.getString(R.string.fs_media_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_media_permission_text)
                }

                Manifest.permission.POST_NOTIFICATIONS -> {
                    tips.title = activity.resources.getString(R.string.fs_notification_permission_title)
                    tips.text = activity.resources.getString(R.string.fs_notification_permission_text)
                }

                else -> {}
            }
            itemData.add(tips)
        }

        fsRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = mPermissionTipsAdapter.apply {
                submitList(itemData)
            }

        }

        fsOk.setOnClickListener {
            dismiss()
            listener?.onClick(it)
        }

        fsCancel.setOnClickListener {
            dismiss()
        }
    }

    class PermissionTipsAdapter : BaseQuickAdapter<PermissionTips, PermissionTipsAdapter.VH>() {
        class VH (parent: ViewGroup, val binding: FsItemPrivacyfloatingtipsBinding = FsItemPrivacyfloatingtipsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )): RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): PermissionTipsAdapter.VH {
           return VH(parent)
        }

        override fun onBindViewHolder(
            holder: PermissionTipsAdapter.VH,
            position: Int,
            item: PermissionTips?
        ) {
            item?.let {
                holder.binding.fsTitle.text = item.title
                holder.binding.fsText.text = item.text
            }
        }

    }


    data class PermissionTips(
            var title: String = "",
            var text: String = ""
    )
}