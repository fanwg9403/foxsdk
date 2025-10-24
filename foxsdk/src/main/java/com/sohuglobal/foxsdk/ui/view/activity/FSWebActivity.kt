package com.sohuglobal.foxsdk.ui.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import com.hjq.toast.Toaster
import com.sohuglobal.foxsdk.R
import com.sohuglobal.foxsdk.utils.FoxSdkUtils
import com.sohuglobal.foxsdk.utils.onClick

/**
 * @Author FHL
 * @CreateTime 2025年 10月 20日 10点 17 分
 * @Desc TODO:
 */
class FSWebActivity : FragmentActivity() {
    var mWebView: WebView? = null
    var url: String? = null
    var html: String? = null

    //是否显示title
    private var showTitle = false
    private var isInit = false
    private var proxyBack = false
    private var needFinish = false

    //是否显示右边的按钮
    private var showReport = false
    var title: TextView? = null
    var tvBack: TextView? = null
    var tvClose: TextView? = null
    var tvReport: TextView? = null
    var titleBar: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fs_activity_web_detail)
        url = intent.getStringExtra("url")
        html = intent.getStringExtra("html")
        showTitle = intent.getBooleanExtra("showTitle", false)
        showReport = intent.getBooleanExtra("showReport", false)
        title = findViewById(R.id.fs_tv_title)
        tvReport = findViewById(R.id.fs_tv_report)
        titleBar = findViewById(R.id.fs_title_bar)
        tvBack = findViewById(R.id.fs_tv_back)
        tvClose = findViewById(R.id.fs_tv_close)

        if (showTitle) {
            titleBar?.visibility = View.VISIBLE
            tvBack?.onClick { onBackPressed() }
            tvClose?.onClick {
                finish()
            }
        } else {
            titleBar?.visibility = View.GONE
        }

        if (url?.isNotEmpty() == true || html?.isNotEmpty() == true) {
            init(url ?: "")
        } else {
            Toaster.show(R.string.fs_link_error)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, true) {
            proxyBackPress()
        }
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    private fun init(url: String) {
        mWebView = findViewById(R.id.fs_web_view)

        val webSettings = mWebView?.settings
        //设置为可调用js方法
        webSettings?.javaScriptEnabled = true
        webSettings?.setSupportZoom(false)
        webSettings?.builtInZoomControls = false
        webSettings?.useWideViewPort = false
        webSettings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings?.loadWithOverviewMode = false
        webSettings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        mWebView?.clearCache(true)

        mWebView?.canGoBack()
        mWebView?.canGoForward()


        webSettings?.javaScriptEnabled = true
        webSettings?.domStorageEnabled = true

        // 设置允许JS弹窗
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        mWebView?.addJavascriptInterface(this, "AndroidFunction")

        if (html != null) {
            mWebView?.loadData(
                """
                            <html>
                              <head>
                                <meta charset="UTF-8" />
                                <title>入驻协议</title>
                              </head>
                              <body>
                            
                            """.trimIndent() + html + """
                             </body>
                            <html>
                            
                            """.trimIndent(),
                "text/html",
                "utf-8"
            )
        } else {
            var copyUrl = url
            if (!copyUrl.contains("alipay")) {
                copyUrl = if (copyUrl.contains("?") && !copyUrl.contains("os=app")) {
                    "$copyUrl&os=app"
                } else {
                    "$copyUrl?os=app"
                }
            }
            mWebView?.loadUrl(copyUrl)
        }
        FoxSdkUtils.runOnUIThreadDelay(500) { isInit = true }
    }

    @JavascriptInterface
    fun finishActivity() {
        this.finish()
    }


    private fun proxyBackPress() {
        if (proxyBack) {
            registerFunction("nativeBack", "back", null)
            registerFunction("nativeHandlerBack", "back", null)
        } else {
            //告知web端app端触发了返回
            registerFunction("isAppBackFun", null, null)
            if (mWebView?.canGoBack() == true) {
                mWebView?.goBack()
            } else {
                finishActivity()
//                super.onBackPressed()
            }
        }
    }

    @JavascriptInterface
    fun registerFunction(funName: String, data: String?, callback: ValueCallback<String?>?) {
        val script = "javascript:$funName(\"$data\")"
        mWebView?.evaluateJavascript(script, callback)
    }

    protected override fun onResume() {
        super.onResume()
        if (needFinish) finish()
    }

    companion object {
        fun startWithUrl(context: Context, url: String, showTitle: Boolean = false) {
            val intent = Intent(context, FSWebActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("showTitle", showTitle)
            context.startActivity(intent)
        }

        fun startWithHtml(context: Context, html: String, showTitle: Boolean = false) {
            val intent = Intent(context, FSWebActivity::class.java)
            intent.putExtra("html", html)
            intent.putExtra("showTitle", showTitle)
            context.startActivity(intent)
        }
    }
}