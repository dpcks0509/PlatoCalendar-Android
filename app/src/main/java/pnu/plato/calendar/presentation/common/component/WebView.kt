package pnu.plato.calendar.presentation.common.component

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(
    url: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            WebView(it).apply {
                settings.apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadUrl(url)
            }
        },
        modifier = modifier,
    )
}
