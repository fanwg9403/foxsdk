package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author FHL
 * @CreateTime 2025年 10月 17日 09点 13 分
 * @Desc 系统消息（公告）
 */
data class FSMessage(
    @SerializedName("created_time")
    val createdTime: String? = null,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("mail_id")
    val mailId: Long? = null,
    val id: Long? = null,
    val img: String? = null,

    @SerializedName("mail_content")
    val mailContent: String? = null,

    @SerializedName("mail_title")
    val mailTitle: String? = null,

    /**
     * 状态，0未读 1已读
     */
    var is_read: Int? = null,

    val type: Long? = null,
    val users: String? = null,

    @SerializedName("users_name_list")
    val usersNameList: String? = null,
) {
    val isRead: Boolean get() = is_read == 1

    val title: String? get() = mailTitle

    val content: String? get() = mailContent
    val time: String? get() = createdTime
}
