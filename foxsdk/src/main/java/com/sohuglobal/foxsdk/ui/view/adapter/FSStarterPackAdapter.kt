package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSStarterPack
import com.sohuglobal.foxsdk.databinding.FsItemStarterPackBinding
import com.sohuglobal.foxsdk.utils.dp2px

/**
 * @file FSStarterPackAdapter
 * 文件说明：新手礼包列表适配器
 *
 * @author 王金强
 * @date 2025/10/14 8:59
 */
class FSStarterPackAdapter : BaseQuickAdapter<FSStarterPack, FSStarterPackAdapter.VH>() {
    class VH(
        parent: ViewGroup, val binding: FsItemStarterPackBinding = FsItemStarterPackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FSStarterPackAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: FSStarterPackAdapter.VH,
        position: Int,
        item: FSStarterPack?
    ) {
        item?.let {
            Glide.with(context)
                .load(item.img)
                .error(R.mipmap.fs_default_avatar_round)
                .into(holder.binding.fsRoundedImage)
            holder.binding.fsTvTitle.text = item.mail_title
            holder.binding.fsTvContent.text = item.mail_content
            val colorsborder = intArrayOf(
                "#8092FFD2".toColorInt(),
                "#0027B178".toColorInt(),
                "#0027B178".toColorInt(),
                "#8092FFD2".toColorInt()
            )
            holder.binding.fsShpTvType.shapeDrawableBuilder.setStrokeGradientColors(colorsborder)
                .setStrokeSize(context.dp2px(1))
                .intoBackground()
            when(item.status){
                1->{//未领取
                    holder.binding.fsShpTvOn.text = context.getString(R.string.fs_str_claim_and_copy)
                    holder.binding.fsShpTvType.visibility = ViewGroup.GONE
                    holder.binding.fsShpTvOn.shapeDrawableBuilder.setSolidColor("#ff009E5C".toColorInt()).intoBackground()
                }
                else->{
                    holder.binding.fsShpTvOn.text = context.getString(R.string.fs_str_copy_code)
                    holder.binding.fsShpTvType.visibility = ViewGroup.VISIBLE

                    holder.binding.fsShpTvOn.shapeDrawableBuilder.setSolidColor("#ff02B8AC".toColorInt()).intoBackground()
                }
            }



        }
    }
}