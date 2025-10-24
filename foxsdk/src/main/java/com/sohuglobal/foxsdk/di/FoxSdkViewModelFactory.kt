package com.sohuglobal.foxsdk.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sohuglobal.foxsdk.ui.viewmodel.FSGameRecordViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSHomeViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSMessageViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSRechargeRecordViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSStarterPackViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSUserFollowsViewModel
import com.sohuglobal.foxsdk.ui.viewmodel.FSWinFoxCoinViewModel

/**
 *
 * 主要功能: ViewModel工厂
 * @Description:
 * @author: 范为广
 * @date: 2025年10月12日 9:55
 */
class FoxSdkViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FSUserFollowsViewModel::class.java) -> {
                val userRepository = FoxSdkRepositoryContainer.getUserRepository()
                FSUserFollowsViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(FSStarterPackViewModel::class.java) -> {
                val starterPackRepository = FoxSdkRepositoryContainer.getStarterPackRepository()
                FSStarterPackViewModel(starterPackRepository) as T
            }
            modelClass.isAssignableFrom(FSHomeViewModel::class.java) -> {
                val homeRepository = FoxSdkRepositoryContainer.getHomeRepository()
                FSHomeViewModel(homeRepository) as T
            }
            modelClass.isAssignableFrom(FSWinFoxCoinViewModel::class.java) -> {
                val starterPackRepository = FoxSdkRepositoryContainer.getWinFoxCoinRepository()
                FSWinFoxCoinViewModel(starterPackRepository) as T
            }
            modelClass.isAssignableFrom(FSRechargeRecordViewModel::class.java) -> {
                val rechargeRecordRepository = FoxSdkRepositoryContainer.getRechargeRecordRepository()
                FSRechargeRecordViewModel(rechargeRecordRepository) as T
            }
            modelClass.isAssignableFrom(FSGameRecordViewModel::class.java) -> {
                val gameRecordRepository = FoxSdkRepositoryContainer.getGameRecordRepository()
                FSGameRecordViewModel(gameRecordRepository) as T
            }
            modelClass.isAssignableFrom(FSMessageViewModel::class.java) -> {
                val messageRepository = FoxSdkRepositoryContainer.getMessageRepository()
                FSMessageViewModel(messageRepository) as T
            }

            else -> throw IllegalArgumentException("未知的 ViewModel class: ${modelClass.name}，请先在FoxSdkRepositoryContainer中注册")
        }
    }
}