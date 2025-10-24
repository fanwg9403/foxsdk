package com.sohuglobal.foxsdk.data.model.entity

/**
 * @file FSGameRecord
 * 文件说明：游戏记录实体
 *
 * @author 王金强
 * @date 2025/10/17 9:41
 */
data class FSGameRecord(
    val app_id: Int,
    val app_name: String,
    val create_time: String,
    val hss_user_id: Int,
    val id: Int,
    val last_play_time: String,
    val play_duration: Int,
    val server_id: Any,
    val server_name: String?=null,
    val update_time: String,
    val game_index_img_file_name: String
)