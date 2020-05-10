package com.example.pr_cbs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView

//    val extendBook = "https://forms.yandex.ru/u/5d7b923c4398960967b56616/?iframe=1"
//        val ask_librarian =

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)





        val intent = intent

        val url = intent.getStringExtra("link")



        webView = findViewById(R.id.webview)
        webView.isVerticalScrollBarEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        if (url != null)  webView.loadUrl(url)
        else  webView.loadUrl("https://pr-cbs.ru")

    }
}
