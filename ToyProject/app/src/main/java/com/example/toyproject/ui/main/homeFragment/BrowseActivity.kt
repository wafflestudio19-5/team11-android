package com.example.toyproject.ui.main.homeFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityBrowseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrowseActivity :AppCompatActivity(){


    private lateinit var binding : ActivityBrowseBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_fade_away)

        val toolbar = binding.browserToolbar
        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.home_setting_exit_button -> {
                    finish()
                    overridePendingTransition(R.anim.slide_fade_away, R.anim.slide_out_up)
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.webView.apply {
            webViewClient = WebViewClient()

            webChromeClient = object : WebChromeClient() {

                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?
                ): Boolean {
                    val newWebVIew = WebView(this@BrowseActivity).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }
                    val dialog = Dialog(this@BrowseActivity).apply {
                        setContentView(newWebVIew)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebVIew.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebVIew
                    resultMsg.sendToTarget()
                    return true
                }
            }
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls =  true

            settings.cacheMode =
                WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.displayZoomControls = true
        }

        val url = intent.getStringExtra("url")
        binding.webView.loadUrl(url.toString())
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_fade_away, R.anim.slide_out_up)
    }
}