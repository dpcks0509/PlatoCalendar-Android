package pusan.university.plato_calendar.presentation.common.component

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

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
                            val url = request?.url.toString()
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val packageName = intent.`package`

                            return if (url.startsWith("intent://")) {
                                if (!packageName.isNullOrBlank()) {
                                    val existPackage = context.packageManager
                                        .getLaunchIntentForPackage(packageName)

                                    if (existPackage != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val marketIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            "market://details?id=$packageName".toUri()
                                        )
                                        context.startActivity(marketIntent)
                                    }
                                }

                                true
                            } else if (url.startsWith("market://")) {
                                val marketIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    url.toUri()
                                )
                                context.startActivity(marketIntent)

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
