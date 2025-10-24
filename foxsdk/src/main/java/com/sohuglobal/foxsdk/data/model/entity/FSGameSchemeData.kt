package com.sohuglobal.foxsdk.data.model.entity

/**
 * @file FSGameScheme
 * 文件说明：游戏sdk跳转许愿狐传参
 *
 * @author 王金强
 * @date 2025/10/23 9:39
 */
data class FSGameSchemeData(
    val type: String,
    val taskNumer: String,
    val tsaskType: String,
    val isNeedCheckTaskNumber: Boolean = true,
    val expand: String=""
)