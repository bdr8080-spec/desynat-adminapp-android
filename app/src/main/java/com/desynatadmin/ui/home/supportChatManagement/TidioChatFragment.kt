package com.desynatadmin.ui.home.supportChatManagement

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentTidioChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TidioChatFragment : BaseFragment() {
    private var binding: FragmentTidioChatBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTidioChatBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.chats)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.settings.mixedContentMode =
                    WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            webView.settings.loadsImagesAutomatically = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true

            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.databaseEnabled = true
            webView.settings.allowFileAccess = true
            webView.settings.allowContentAccess = true

            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()


         //   webView.loadUrl("file:///android_asset/tidio_chat.html")

            webView?.loadDataWithBaseURL(
                "https://www.tidio.com",
                """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <script src="https://code.tidio.co/na5y0weklpcd9uxhccfezplsfg0fggvk.js"></script>

        <script>
            document.addEventListener("tidioChat-ready", function () {
                if (!window.tidioChatApi) return;

                var name = "Test";
                var email = "test@admin.com";
                var role = "admin";
                var uniqueId = "admin + "_" + test@admin.com;

                window.tidioChatApi.setVisitorData({
                    name: name,
                    email: email,
                    distinct_id: uniqueId,
                    role: role
                });

                window.tidioChatApi.open();
            });
        </script>
    </head>

    <body></body>
    </html>
    """.trimIndent(),
                "text/html",
                "UTF-8",
                null
            )

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    android.util.Log.d("WEBVIEW", "Page Loaded")
                }

                override fun onReceivedError(
                    view: android.webkit.WebView?,
                    request: android.webkit.WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    android.util.Log.e("WEBVIEW", "Error: ${error?.description}")
                }
            }
        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {
    }

}