package pnu.plato.calendar.presentation.common.component

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    url: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                webViewClient =
                    object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?,
                        ): Boolean {
                            val loadingUrl = request?.url.toString()

                            return if (loadingUrl.startsWith("intent://")) {
                                val intent = Intent.parseUri(loadingUrl, Intent.URI_INTENT_SCHEME)
                                context.startActivity(intent)

                                true
                            } else {
                                false
                            }
                        }
                    }

                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
    )
}
