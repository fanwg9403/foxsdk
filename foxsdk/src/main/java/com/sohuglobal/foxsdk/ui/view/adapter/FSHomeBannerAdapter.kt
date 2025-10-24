package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.sohuglobal.foxsdk.data.model.entity.FSHomeBanner
import com.sohuglobal.foxsdk.databinding.FsLayoutHomeBannerBinding

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 10点 53 分
 * @Desc 首页单视图轮播适配器
 */
class FSHomeBannerAdapter : BaseSingleItemAdapter<List<FSHomeBanner>, FSHomeBannerAdapter.VH>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, item: List<FSHomeBanner>?) {
        if (item != null && item.isNotEmpty()) {
            holder.binding.fsHomeBanner.isVisible = true
            val mAdapter = FSBannerAdapter()
            holder.binding.fsHomeBanner.apply {
                setAdapter(mAdapter)
                mAdapter.setDatas(item)
            }
        } else {
            holder.binding.fsHomeBanner.isVisible = false
        }

    }

    class VH(
        parent: ViewGroup,
        val binding: FsLayoutHomeBannerBinding = FsLayoutHomeBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)
}



