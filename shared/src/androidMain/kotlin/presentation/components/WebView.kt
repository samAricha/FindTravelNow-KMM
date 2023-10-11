package presentation.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView as AndroidWebView

private const val DESKTOP_USER_AGENT2 =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebView(modifier: Modifier, url: String) {
    Box(modifier = modifier) {
        var isLoading by remember { mutableStateOf(true) }
        AndroidView(
            factory = {
                AndroidWebView(it).apply {
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            super.onReceivedError(view, request, error)
                            println("Error while loading Web: ${error?.description}")
//                            view?.loadUrl("about:blank")
                            isLoading = false
                        }
                    }
                    isHorizontalScrollBarEnabled = true
                    with(settings) {
                        javaScriptEnabled = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        domStorageEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                    }
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    loadUrl(url)
                }
            }, update = {}
        )

        if (isLoading)
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary
            )
    }
}