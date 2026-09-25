package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.viewmodel.ViewportMode

class WebAppInterface(private val onPageNavigated: (String) -> Unit) {
    @JavascriptInterface
    fun navigateToPage(pageName: String) {
        onPageNavigated(pageName)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPreview(
    htmlContent: String,
    viewportMode: ViewportMode,
    onNavigatePage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val boxModifier = when (viewportMode) {
        ViewportMode.MOBILE -> modifier
            .width(375.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
        ViewportMode.TABLET -> modifier
            .width(680.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
        ViewportMode.DESKTOP -> modifier.fillMaxWidth()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("webview_preview_container"),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(modifier = boxModifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {}
                        addJavascriptInterface(WebAppInterface(onNavigatePage), "AndroidBridge")
                        loadDataWithBaseURL("https://webforge.app/", htmlContent, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://webforge.app/", htmlContent, "text/html", "UTF-8", null)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_engine")
            )
        }
    }
}
