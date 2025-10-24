package com.sohuglobal.foxsdk.data.network.interceptor

import android.util.Log
import com.sohuglobal.foxsdk.core.FoxSdkConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * 主要功能: 详细的网络请求日志拦截器
 * @Description: 与SdkConfig.enableLog关联，控制是否打印详细日志
 * @author: 范为广
 * @date: 2025年10月11日 16:58
 */
class FoxSdkLoggingInterceptor(private val config: FoxSdkConfig) : Interceptor {

    companion object {
        // 日志TAG
        private const val TAG = "FoxSdk[Http]"

        // Logcat单条日志最大长度
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // 如果不启用日志，直接执行请求
        if (!config.enableLog)
            return chain.proceed(chain.request())

        val request = chain.request()
        val requestStartTime = System.currentTimeMillis()

        // 打印请求信息
        logRequest(request)

        // 执行请求
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            logException(e, requestStartTime)
            throw e
        }

        // 打印响应信息
        logResponse(response, requestStartTime)

        return response
    }

    /**
     * 打印请求信息
     */
    private fun logRequest(request: Request) {
        try {
            val logBuilder = StringBuilder()
            logBuilder.append("\n")
            logBuilder.append("╔════════════════════════════════════════════════════════════════════════════════════════\n")
            logBuilder.append("║ 🌐 HTTP REQUEST\n")
            logBuilder.append("╠════════════════════════════════════════════════════════════════════════════════════════\n")
            logBuilder.append("║ Method: ${request.method}\n")
            logBuilder.append("║ URL: ${request.url}\n")

            // 打印请求头
            val headers = request.headers
            if (headers.size > 0) {
                logBuilder.append("║ \n")
                logBuilder.append("║ Headers:\n")
                headers.forEach { header ->
                    logBuilder.append("║   ${header.first}: ${header.second}\n")
                }
            }

            // 打印请求体
            request.body?.let { body ->
                logBuilder.append("║ \n")
                logBuilder.append("║ Body:\n")

                val buffer = Buffer()
                body.writeTo(buffer)

                val contentType = body.contentType()
                val contentLength = body.contentLength()

                logBuilder.append("║   Content-Type: $contentType\n")
                logBuilder.append("║   Content-Length: $contentLength\n")

                if (body.contentLength() != 0L) {
                    logBuilder.append("║   \n")

                    when {
                        contentType?.toString()?.contains("application/json") == true -> {
                            val jsonString = buffer.readUtf8()
                            val formattedJson = try {
                                JSONObject(jsonString).toString(2)
                            } catch (e: Exception) {
                                try {
                                    JSONArray(jsonString).toString(2)
                                } catch (e2: Exception) {
                                    jsonString
                                }
                            }
                            logBuilder.append("║   ${formatJsonForLogging(formattedJson)}\n")
                        }

                        contentType?.toString()?.contains("form-data") == true -> {
                            val jsonString = buffer.readUtf8()
                            val formattedJson = try {
                                JSONObject(jsonString).toString(2)
                            } catch (e: Exception) {
                                try {
                                    JSONArray(jsonString).toString(2)
                                } catch (e2: Exception) {
                                    jsonString
                                }
                            }
                            logBuilder.append("║   ${formatJsonForLogging(formattedJson)}\n")
                        }

                        contentType?.toString()?.contains("x-www-form-urlencoded") == true -> {
                            val formString = buffer.readUtf8()
                            logBuilder.append("║   ${formatFormDataForLogging(formString)}\n")
                        }

                        else -> {
                            logBuilder.append("║   [Binary Data - Content-Type: $contentType]\n")
                        }
                    }
                }
            }

            logBuilder.append("╚════════════════════════════════════════════════════════════════════════════════════════\n")

            logChunked(TAG, logBuilder.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log request", e)
        }
    }

    /**
     * 打印响应信息
     */
    private fun logResponse(response: Response, requestStartTime: Long) {
        try {
            val responseTime = System.currentTimeMillis() - requestStartTime
            val logBuilder = StringBuilder()
            logBuilder.append("\n")
            logBuilder.append("╔════════════════════════════════════════════════════════════════════════════════════════\n")
            logBuilder.append("║ 📡 HTTP RESPONSE\n")
            logBuilder.append("╠════════════════════════════════════════════════════════════════════════════════════════\n")
            logBuilder.append("║ URL: ${response.request.url}\n")
            logBuilder.append("║ Status: ${response.code} ${response.message}\n")
            logBuilder.append("║ Response Time: ${responseTime}ms\n")
            logBuilder.append("║ Protocol: ${response.protocol}\n")

            // 打印响应头
            val headers = response.headers
            if (headers.size > 0) {
                logBuilder.append("║ \n")
                logBuilder.append("║ Headers:\n")
                headers.forEach { header ->
                    logBuilder.append("║   ${header.first}: ${header.second}\n")
                }
            }

            // 打印响应体
            val responseBody = response.body
            if (responseBody != null) {
                logBuilder.append("║ \n")
                logBuilder.append("║ Body:\n")

                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer()

                val contentType = responseBody.contentType()
                val contentLength = responseBody.contentLength()

                logBuilder.append("║   Content-Type: $contentType\n")
                logBuilder.append("║   Content-Length: $contentLength\n")

                if (contentLength != 0L) {
                    logBuilder.append("║   \n")

                    val content = buffer.clone().readUtf8()

                    when {
                        contentType?.toString()?.contains("application/json") == true -> {
                            val formattedJson = try {
                                JSONObject(content).toString(2)
                            } catch (e: Exception) {
                                try {
                                    JSONArray(content).toString(2)
                                } catch (e2: Exception) {
                                    content
                                }
                            }
                            logBuilder.append("║   ${formatJsonForLogging(formattedJson)}\n")
                        }

                        contentType?.toString()?.contains("text/plain") == true -> {
                            logBuilder.append("║   ${formatTextForLogging(content)}\n")
                        }

                        contentType?.toString()?.contains("html") == true -> {
                            logBuilder.append("║   [HTML Content - Length: ${content.length}]\n")
                            // 可以选择性地打印HTML的前几行
                            val lines = content.lines().take(5)
                            if (lines.isNotEmpty()) {
                                logBuilder.append("║   Preview:\n")
                                lines.forEach { line ->
                                    logBuilder.append("║     $line\n")
                                }
                                if (content.lines().size > 5) {
                                    logBuilder.append("║     ... (truncated)\n")
                                }
                            }
                        }

                        else -> {
                            logBuilder.append("║   [Binary Data - Content-Type: $contentType, Length: $contentLength]\n")
                        }
                    }
                }
            }

            logBuilder.append("╚════════════════════════════════════════════════════════════════════════════════════════\n")

            logChunked(TAG, logBuilder.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log response", e)
        }
    }

    /**
     * 打印异常信息
     */
    private fun logException(e: Exception, requestStartTime: Long) {
        val responseTime = System.currentTimeMillis() - requestStartTime
        val logBuilder = StringBuilder()
        logBuilder.append("\n")
        logBuilder.append("╔════════════════════════════════════════════════════════════════════════════════════════\n")
        logBuilder.append("║ ❌ HTTP REQUEST FAILED\n")
        logBuilder.append("╠════════════════════════════════════════════════════════════════════════════════════════\n")
        logBuilder.append("║ Response Time: ${responseTime}ms\n")
        logBuilder.append("║ Exception: ${e.javaClass.simpleName}\n")
        logBuilder.append("║ Message: ${e.message}\n")

        // 打印堆栈信息（只打印前5行）
        val stackTrace = e.stackTraceToString().lines().take(5)
        if (stackTrace.isNotEmpty()) {
            logBuilder.append("║ \n")
            logBuilder.append("║ StackTrace (first 5 lines):\n")
            stackTrace.forEach { line ->
                logBuilder.append("║   $line\n")
            }
        }

        logBuilder.append("╚════════════════════════════════════════════════════════════════════════════════════════\n")

        logChunked(TAG, logBuilder.toString())
    }

    /**
     * 格式化JSON用于日志输出
     */
    private fun formatJsonForLogging(json: String): String {
        return json.lines().joinToString("\n║   ") { line ->
            line
        }
    }

    /**
     * 格式化表单数据用于日志输出
     */
    private fun formatFormDataForLogging(formData: String): String {
        return formData.split("&").joinToString("\n║     ") { param ->
            val parts = param.split("=")
            if (parts.size == 2) {
                "${parts[0]}: ${parts[1]}"
            } else {
                param
            }
        }
    }

    /**
     * 格式化文本用于日志输出
     */
    private fun formatTextForLogging(text: String): String {
        return text.lines().joinToString("\n║   ") { line ->
            line
        }
    }

    /**
     * 分块打印长日志（解决Android Logcat单条日志长度限制）
     */
    private fun logChunked(tag: String, message: String) {
        if (message.length <= MAX_LOG_LENGTH) {
            Log.d(tag, message)
            return
        }

        // 分块打印
        var start = 0
        while (start < message.length) {
            var end = start + MAX_LOG_LENGTH
            if (end > message.length) {
                end = message.length
            }

            // 尽量在行边界处分割
            var chunk = message.substring(start, end)
            if (end < message.length) {
                val lastNewLine = chunk.lastIndexOf('\n')
                if (lastNewLine != -1 && lastNewLine > chunk.length - 100) {
                    chunk = chunk.substring(0, lastNewLine + 1)
                    end = start + lastNewLine + 1
                }
            }

            Log.d(tag, chunk)
            start = end
        }
    }
}