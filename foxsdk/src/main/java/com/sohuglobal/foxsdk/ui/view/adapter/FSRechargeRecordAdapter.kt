package com.sohuglobal.foxsdk.ui.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSRechargeRecord
import com.sohuglobal.foxsdk.databinding.FsItemRechargeRecordBinding

/**
 * @file FSRechargeRecordAdapter
 * 文件说明：充值记录列表适配器
 *
 * @author 王金强
 * @date 2025/10/16 19:23
 */
class FSRechargeRecordAdapter : BaseQuickAdapter<FSRechargeRecord, FSRechargeRecordAdapter.VH>() {
    class VH(
        parent: ViewGroup, val binding: FsItemRechargeRecordBinding = FsItemRechargeRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FSRechargeRecordAdapter.VH {
        return VH(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: FSRechargeRecordAdapter.VH,
        position: Int,
        item: FSRechargeRecord?
    ) {
        item?.let {
            Glide.with(context)
                .load("")
                .error(R.mipmap.fs_default_avatar_round)
                .into(holder.binding.fsRoundedImage)
            holder.binding.fsTvTitle.text = it.mallName
            holder.binding.fsTvContent.text = it.mallSku
            holder.binding.fsTvFoxCoin.text = "￥${it.price}"
            holder.binding.fsShpTvTime.text = it.createTime

        }
    }
}