package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSWinFoxCoin
import com.sohuglobal.foxsdk.databinding.FsItemWinFoxCoinBinding

/**
 * @file FSWinFoxCoinAdapter
 * 文件说明：赢狐币列表适配器
 *
 * @author 王金强
 * @date 2025/10/14 15:34
 */
class FSWinFoxCoinAdapter : BaseQuickAdapter<FSWinFoxCoin, FSWinFoxCoinAdapter.VH>(){
    class VH( parent: ViewGroup, val binding: FsItemWinFoxCoinBinding = FsItemWinFoxCoinBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FSWinFoxCoinAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: FSWinFoxCoinAdapter.VH,
        position: Int,
        item: FSWinFoxCoin?
    ) {
        item?.let {
            Glide.with(context)
                .load(handleCoverImage(it.image_url, it.industry_image_url))
                .error(R.mipmap.fs_default_avatar_round)
                .into(holder.binding.fsRoundedImage)
            holder.binding.fsTvTitle.text = it.title
            holder.binding.fsTvContent.text = it.content
            holder.binding.fsTvPrice.text = it.full_amount
            holder.binding.fsTvCoinNum.text = "${it.give_coin}"

        }
    }

    /**
    愿望封面图加载规则处理
    封面图->分类图->默认图
     */
    private fun handleCoverImage(coverImge: String?, industryImageUrl: String?): String {
        return if (coverImge.isNullOrBlank()) industryImageUrl ?: "" else coverImge.split(",")[0]
    }
}