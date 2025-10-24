package com.sohuglobal.foxsdk.data.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author FHL
 * @CreateTime 2025年 10月 20日 11点 37 分
 * @Desc TODO:
 */
data class FSPageContainer<T>(
    @SerializedName("current_page")
    val currentPage: Int? = null,

    val data: ArrayList<T>? = null,

    @SerializedName("last_page")
    val lastPage: Int? = null,

    @SerializedName("per_page")
    val perPage: Int? = null,

    /**
     * 总数据量
     */
    val total: Int = 0
)