package com.on.staccato.presentation.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import com.on.staccato.R
import com.on.staccato.databinding.ActivityWebviewBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CloseToolbarHandler
import com.on.staccato.presentation.util.showToast

class WebViewActivity :
    BindingActivity<ActivityWebviewBinding>(),
    CloseToolbarHandler {
    override val layoutResourceId: Int = R.layout.activity_webview
    private val webView: WebView by lazy { binding.webview }
    private var url: String? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBindings()
        getUrlFromIntent()
        setupWebView()
        loadWebView()
    }

    private fun initBindings() {
        binding.toolbarTitle = intent.getStringExtra(EXTRA_TOOLBAR_TITLE)
        binding.toolbarHandler = this
    }

    private fun getUrlFromIntent() {
        url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrEmpty()) {
            showToast(getString(R.string.all_webview_failure))
            finish()
        }
    }

    private fun setupWebView() {
        webView.webViewClient = WebViewClient()
        webView.settings.textZoom = DEFAULT_WEBVIEW_TEXT_SIZE
    }

    private fun loadWebView() {
        url?.let { webView.loadUrl(it) }
    }

    override fun onCloseClicked() {
        finish()
    }

    companion object {
        const val EXTRA_TOOLBAR_TITLE = "toolbar_title"
        const val EXTRA_URL = "url"
        private const val DEFAULT_WEBVIEW_TEXT_SIZE = 100

        fun launch(
            context: Context,
            url: String,
            toolbarTitle: String,
        ) {
            val intent = Intent(context, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle)
            context.startActivity(intent)
        }
    }
}
