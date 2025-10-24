package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSGameRecord
import com.sohuglobal.foxsdk.databinding.FsItemGameRecordBinding

/**
 * @file FSRechargeRecordAdapter
 * 文件说明：游戏记录列表适配器
 *
 * @author 王金强
 * @date 2025/10/16 19:23
 */
class FSGameRecordAdapter : BaseQuickAdapter<FSGameRecord, FSGameRecordAdapter.VH>() {
    class VH(
        parent: ViewGroup, val binding: FsItemGameRecordBinding = FsItemGameRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FSGameRecordAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: FSGameRecordAdapter.VH,
        position: Int,
        item: FSGameRecord?
    ) {
        item?.let {
            Glide.with(context)
                .load(it.game_index_img_file_name)
                .error(R.mipmap.fs_default_avatar_round)
                .into(holder.binding.fsRoundedImage)
            holder.binding.fsTvTitle.text = it.app_name
            holder.binding.fsTvContent.text = it.server_name

        }
    }
}