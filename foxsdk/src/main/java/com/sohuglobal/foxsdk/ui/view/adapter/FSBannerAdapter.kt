package com.sohuglobal.foxsdk.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohuglobal.foxsdk.data.model.entity.FSHomeBanner
import com.sohuglobal.foxsdk.databinding.FsLayoutHomeBannerItemBinding
import com.sohuglobal.foxsdk.ui.view.activity.FSWebActivity
import com.sohuglobal.foxsdk.utils.FSGlideRoundTransform
import com.sohuglobal.foxsdk.utils.onClick
import com.youth.banner.adapter.BannerAdapter

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 12点 03 分
 * @Desc TODO:
 */
class FSBannerAdapter(data: List<FSHomeBanner> = listOf()) :
    BannerAdapter<FSHomeBanner, FSBannerAdapter.VH>(data) {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): VH? {
        return VH(parent)
    }

    override fun onBindView(holder: VH, data: FSHomeBanner, p: Int, size: Int) {
        holder.binding.fsBannerImage.let { it ->
            Glide.with(it.context)
                .load(data.image)
                .transform(FSGlideRoundTransform(it.context, 9))
                .into(it)
            it.onClick {
                FSWebActivity.startWithUrl(it.context, data.objLink ?: "")
            }
        }
    }

    class VH(
        parent: ViewGroup,
        val binding: FsLayoutHomeBannerItemBinding = FsLayoutHomeBannerItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}