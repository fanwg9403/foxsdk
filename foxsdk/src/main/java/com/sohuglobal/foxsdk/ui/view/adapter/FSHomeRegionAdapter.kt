package com.sohuglobal.foxsdk.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.databinding.FsLayoutHomeRegionBinding
import com.sohuglobal.foxsdk.domain.intent.FSHomeIntent
import com.sohuglobal.foxsdk.ui.view.activity.FSStarterPackActivity
import com.sohuglobal.foxsdk.ui.view.activity.FSWinFoxCoinActivity
import com.sohuglobal.foxsdk.ui.view.dialog.FSLoginDialog
import com.sohuglobal.foxsdk.ui.viewmodel.FSHomeViewModel
import com.sohuglobal.foxsdk.utils.customerservice.QiyukfHelper
import com.sohuglobal.foxsdk.utils.onClick

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 12点 06 分
 * @Desc 礼包、充值、客服
 */
class FSHomeRegionAdapter(val viewModel: FSHomeViewModel) :
    BaseSingleItemAdapter<String, FSHomeRegionAdapter.VH>(mItem = "") {
    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: VH,
        item: String?
    ) {
        holder.binding.apply {
            fsIvCoin.onClick {
                if (FSUserInfo.getInstance() == null) {
                    FSLoginDialog(context)
                        .setOnLoginClickListener { arg1, arg2, type ->
                            viewModel.dispatch(FSHomeIntent.Login(arg1, arg2, type))
                        }
                        .show()
                } else {
                    FSWinFoxCoinActivity.start(context)
                }
            }
            fsIvGift.onClick {
                if (FSUserInfo.getInstance() == null) {
                    FSLoginDialog(context)
                        .setOnLoginClickListener { arg1, arg2, type ->
                            viewModel.dispatch(FSHomeIntent.Login(arg1, arg2, type))
                        }
                        .show()
                } else {
                    FSStarterPackActivity.start(context)
                }
            }
            fsIvService.onClick {
                if (FSUserInfo.getInstance() == null) {
                    FSLoginDialog(context)
                        .setOnLoginClickListener { arg1, arg2, type ->
                            viewModel.dispatch(FSHomeIntent.Login(arg1, arg2, type))
                        }
                        .show()
                } else {
                    QiyukfHelper.instance.openCustomerService(
                        context, "在线客服", "Mine", "", ""
                    )
                }
            }
        }
    }


    class VH(
        parent: ViewGroup,
        val binding: FsLayoutHomeRegionBinding = FsLayoutHomeRegionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root) {
    }
}