package com.example.zx.webviewdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Second2Activity extends AppCompatActivity {

    private WebView webView;
    private String result = "this is result from Android";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second2);

       webView = findViewById(R.id.webview);

      WebSettings settings =  webView.getSettings();

      settings.setJavaScriptEnabled(true);
      settings.setJavaScriptCanOpenWindowsAutomatically(true);
      webView.loadUrl("file:///android_asset/javascript2.html");

      webView.setWebViewClient(new WebViewClient(){
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                  Uri uri = Uri.parse(String.valueOf(request.getUrl()));
                  if (uri.getScheme().equals("js")){
                      if (uri.getAuthority().equals("webview")){
                          System.out.println("js调用了Android的方法-shouldOverrideUrlLoading");
                          // 可以在协议上带有参数并传递到Android上
//                          Set<String> collection = uri.getQueryParameterNames();

//                          webView.loadUrl("javascript2:returnResult(" +  result + ")");
                      }
                  }
                  return true;
              }

              return super.shouldOverrideUrlLoading(view, request);
          }
      });

      webView.setWebChromeClient(new WebChromeClient(){
          @Override
          public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
              new AlertDialog.Builder(Second2Activity.this)
                      .setTitle(message)
                      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              result.confirm();
                          }
                      })
                      .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              result.cancel();
                          }
                      })
                      .setCancelable(false)
                      .show();

              return true;
          }
      });
    }


}
