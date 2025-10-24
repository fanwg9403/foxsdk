package com.sohuglobal.foxsdk.ui.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.data.model.entity.FSCoinInfo
import com.sohuglobal.foxsdk.data.model.entity.FSUserInfo
import com.sohuglobal.foxsdk.databinding.FsLayoutHomeUserInfoBinding
import com.sohuglobal.foxsdk.domain.intent.FSHomeIntent
import com.sohuglobal.foxsdk.ui.view.dialog.FSLoginDialog
import com.sohuglobal.foxsdk.ui.viewmodel.FSHomeViewModel
import com.sohuglobal.foxsdk.utils.onClick

/**
 * @Author FHL
 * @CreateTime 2025年 10月 14日 14点 15 分
 * @Desc TODO:
 */

class FSHomeUserInfoAdapter(val viewModel: FSHomeViewModel) :
    BaseSingleItemAdapter<FSUserInfo, FSHomeUserInfoAdapter.VH>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: VH,
        item: FSUserInfo?
    ) {
        holder.binding.apply {
            if (item != null) {
                fsTvUsername.text = item.userName
                fsStvCoin.isVisible = true
                fsStvCoin.text =
                    "${context.getString(R.string.fs_fox_coin)}${FSCoinInfo.getInstance()?.foxCoin ?: 0}"
            } else {
                fsStvCoin.isVisible = false
                fsTvUsername.text = context.getString(R.string.fs_login_now)
            }

            fsClTopBar.onClick {
                if (item == null) {
                    FSLoginDialog(context)
                        .setOnLoginClickListener { arg1, arg2, type ->
                            viewModel.dispatch(FSHomeIntent.Login(arg1, arg2, type))
                        }
                        .show()
                }
            }

        }
    }

    class VH(
        parent: ViewGroup,
        val binding: FsLayoutHomeUserInfoBinding = FsLayoutHomeUserInfoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
    }
}