package com.sohuglobal.foxsdk.data.model.paging

/**
 *
 * 主要功能: 分页请求参数 - 用于Repository层的数据请求
 * @Description: 位于数据层，封装分页请求的基本参数
 * @author: 范为广
 * @date: 2025年10月12日 10:12
 */
data class FoxSdkPageRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val isRefresh: Boolean = false,
    val isInitial: Boolean = false,
    val parameters: Map<String, Any> = emptyMap()
) {
    /**
     * 创建下一页请求
     */
    fun nextPage(): FoxSdkPageRequest {
        return copy(page = page + 1, isRefresh = false)
    }

    /**
     * 创建刷新请求（回到第一页）
     */
    fun refresh(): FoxSdkPageRequest {
        return copy(page = 1, isRefresh = true)
    }

    /**
     * 添加额外参数
     */
    fun withParameter(key: String, value: Any): FoxSdkPageRequest {
        return copy(parameters = parameters + (key to value))
    }

    /**
     * 获取参数值
     */
    fun getParameter(key: String): Any? {
        return parameters[key]
    }

    /**
     * 获取字符串参数
     */
    fun getStringParameter(key: String): String? {
        return parameters[key] as? String
    }

    /**
     * 获取整型参数
     */
    fun getIntParameter(key: String): Int? {
        return when (val value = parameters[key]) {
            is Int -> value
            is String -> value.toIntOrNull()
            else -> null
        }
    }
}

/**
 * 分页请求常量
 */
object PageConstants {
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE = 100
    const val FIRST_PAGE = 1
}

/**
 * 分页请求构建器
 */
class PageRequestBuilder {
    private var page: Int = PageConstants.FIRST_PAGE
    private var pageSize: Int = PageConstants.DEFAULT_PAGE_SIZE
    private var isRefresh: Boolean = false
    private var isInitial: Boolean = false
    private val parameters = mutableMapOf<String, Any>()

    fun page(page: Int) = apply { this.page = page }
    fun pageSize(pageSize: Int) = apply { this.pageSize = pageSize }
    fun isRefresh(isRefresh: Boolean) = apply { this.isRefresh = isRefresh }
    fun isInitial(isInitial: Boolean) = apply { this.isInitial = isInitial }
    fun parameter(key: String, value: Any) = apply { parameters[key] = value }

    fun build(): FoxSdkPageRequest {
        return FoxSdkPageRequest(page, pageSize, isRefresh, isInitial, parameters.toMap())
    }
}

// 扩展函数用于快速创建PageRequest
fun pageRequest(block: PageRequestBuilder.() -> Unit = {}): FoxSdkPageRequest {
    return PageRequestBuilder().apply(block).build()
}