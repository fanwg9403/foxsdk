package com.sohuglobal.foxsdk.di

import com.sohuglobal.foxsdk.data.repository.FSGameRecordRepository
import com.sohuglobal.foxsdk.data.repository.FSHomeRepository
import com.sohuglobal.foxsdk.data.repository.FSRechargeRecordRepository
import com.sohuglobal.foxsdk.data.repository.FSMessageRepository
import com.sohuglobal.foxsdk.data.repository.FSStarterPackRepository
import com.sohuglobal.foxsdk.data.repository.FSUserRepository
import com.sohuglobal.foxsdk.data.repository.FSWinFoxCoinRepository

/**
 *
 * 主要功能: 依赖注入容器 - 集中管理所有Repository实例
 * @Description: 负责创建和管理数据层组件的生命周期
 * @author: 范为广
 * @date: 2025年10月12日 12:24
 */
object FoxSdkRepositoryContainer {

    private val _userRepository by lazy { FSUserRepository() }

    private val _starterPackRepository by lazy { FSStarterPackRepository() }

    private val _winFoxCoinRepository by lazy { FSWinFoxCoinRepository() }

    private val _homeRepository by lazy { FSHomeRepository() }

    private val _rechargeRecordRepository by lazy { FSRechargeRecordRepository() }

    private val _gameRecordRepository by lazy { FSGameRecordRepository() }

    private val _messageRepository by lazy { FSMessageRepository() }

    /**
     * 获取用户Repository
     */
    fun getUserRepository(): FSUserRepository = _userRepository

    /**
     * 获取新手礼包Repository
     */
    fun getStarterPackRepository(): FSStarterPackRepository = _starterPackRepository

    /**
     * 获取首页Repository
     */
    fun getHomeRepository(): FSHomeRepository = _homeRepository


    /**
     * 获取赢狐币Repository
     */
    fun getWinFoxCoinRepository(): FSWinFoxCoinRepository = _winFoxCoinRepository

    /**
     * 获取消息列表Repository
     */
    fun getMessageRepository(): FSMessageRepository = _messageRepository

    /**
     * 获取充值记录Repository
     */
    fun getRechargeRecordRepository(): FSRechargeRecordRepository = _rechargeRecordRepository

    /**
     * 获取游戏记录Repository
     */
    fun getGameRecordRepository(): FSGameRecordRepository = _gameRecordRepository
}