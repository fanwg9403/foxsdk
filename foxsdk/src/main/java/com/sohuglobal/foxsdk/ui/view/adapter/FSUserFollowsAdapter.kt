package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserProfile
import com.sohuglobal.foxsdk.databinding.FsItemUserFollowsBinding

/**
 *
 * 主要功能: 用户关注列表适配器
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 17:06
 */
class FSUserFollowsAdapter : BaseQuickAdapter<FSUserInfo, FSUserFollowsAdapter.VH>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: FSUserInfo?
    ) {
        item?.let {
            Glide.with(context)
                .load(it.avatar)
                .into(holder.binding.fsIvAvatar)

            holder.binding.fsTvName.text = it.userName
        }
    }

    class VH(
        parent: ViewGroup,
        val binding: FsItemUserFollowsBinding = FsItemUserFollowsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)
}