package com.sohuglobal.foxsdk.data.model.paging

/**
 *
 * 主要功能: 分页API响应模型
 * @Description: 用于那些返回完整分页信息的API
 * @author: 范为广
 * @date: 2025年10月12日 11:22
 */
data class FoxSdkPageResponse<T>(
    val items: List<T>,
    val currentPage: Int = PageConstants.FIRST_PAGE,
    val pageSize: Int = PageConstants.DEFAULT_PAGE_SIZE,
    val totalCount: Int,
    val totalPages: Int,
    val hasNextPage: Boolean
)
