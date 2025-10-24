package com.sohuglobal.foxsdk.data.model.entity

/**
 * @file FSStarterPack
 * 文件说明：新手礼包实体
 *
 * @author 王金强
 * @date 2025/10/13 15:31
 */
data class FSStarterPack(
    val code: String? = null,
    val created_time: String? = null,
    val end_time: Any? = null,
    val id: Long? = null,
    val mail_content: String? = null,
    val mail_title: String? = null,
    var status: Int? = null,
    val type: Long? = null,
    val users: String? = null,
    val users_name_list: String? = null,
    val img: String? = null
)