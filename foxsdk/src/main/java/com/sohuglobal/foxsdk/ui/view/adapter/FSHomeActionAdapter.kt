package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.databinding.FsLayoutHomeActionBinding
import com.sohuglobal.foxsdk.utils.onClick

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 12点 27 分
 * @Desc TODO:
 */
class FSHomeActionAdapter(
    data: List<Pair<String, Int>> = listOf(),
    val itemClickListener: (Int) -> Unit = {}
) : BaseQuickAdapter<Pair<String, Int>, FSHomeActionAdapter.VH>(data) {
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
        item: Pair<String, Int>?
    ) {
        holder.binding.apply {
            fsTvAction.text = item?.first
            fsTvAction.onClick {
                itemClickListener.invoke(item?.second ?: 0)
            }
            fsTvAction.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (item?.second == -1) 0 else R.drawable.fs_icon_right_arrow,
                0,
            )
        }
    }

    class VH(
        parent: ViewGroup,
        val binding: FsLayoutHomeActionBinding = FsLayoutHomeActionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root)
}