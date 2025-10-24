package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.data.model.entity.FSMessage
import com.sohuglobal.foxsdk.databinding.FsItemMessageListBinding
import com.sohuglobal.foxsdk.utils.onClick

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 12点 03 分
 * @Desc 消息列表适配器
 */
class FSMessageListAdapter : BaseQuickAdapter<FSMessage, FSMessageListAdapter.VH>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: FSMessage?) {
        item?.let { it ->
            holder.binding.fsTvTitle.text = it.title
            holder.binding.fsTvContent.text = it.content
            holder.binding.fsTvTime.text = it.time
            holder.binding.fsIvUnread.isVisible = !it.isRead
            holder.binding.fsMessageRoot.onClick {
                onItemClick(it, position)
            }
        }
    }

    class VH(
        parent: ViewGroup,
        val binding: FsItemMessageListBinding = FsItemMessageListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root)
}