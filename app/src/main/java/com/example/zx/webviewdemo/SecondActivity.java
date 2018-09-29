package com.example.zx.webviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SecondActivity extends AppCompatActivity {

    private WebView webView;

    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        webView = findViewById(R.id.webview);

         settings = webView.getSettings();

         webView.addJavascriptInterface(new AndroidtoJs(),"test");

         webView.loadUrl("file:///android_asset/javascript1.html");

    }

    @Override
    protected void onResume() {
        super.onResume();
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settings.setJavaScriptEnabled(false);
    }
}
