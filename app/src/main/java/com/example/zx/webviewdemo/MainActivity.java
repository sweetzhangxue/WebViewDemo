package com.example.zx.webviewdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private LinearLayout container;

    private WebSettings settings;

    private Button mButton,btnSecond,btnSecondTwo,btnSecondThrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        setSettings();
        setWebViewClient();
        setWebChromeClient();

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webView.loadUrl("file:///android_asset/javascript.html");


    }

    private void initView() {
        webView = findViewById(R.id.webview);
        container = findViewById(R.id.ll_container);
        mButton = findViewById(R.id.btn);
        btnSecond = findViewById(R.id.btn_second);
        btnSecondTwo = findViewById(R.id.btn_second_two);
        btnSecondThrid = findViewById(R.id.btn_second_third);

        mButton.setOnClickListener(this);
        btnSecond.setOnClickListener(this);
        btnSecondTwo.setOnClickListener(this);
        btnSecondThrid.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复pauseTimers状态
        webView.resumeTimers();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当应用程序（存在WebView）被切换到后台时，这个方法不仅仅针对房前的WebView而是全局的应用程序的WebView，他会暂停所有WebView的layout、parsing、javascripttimer。降低CPU功耗
        webView.pauseTimers();

        settings.setJavaScriptEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //动态添加webview需要先从容器中移除webview，然后再销毁webview
//        container.removeView(webView);
        webView.destroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理各种通知&请求事件
     */
    public void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//设定加载开始的操作
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {//设定加载结束的操作
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {//在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
                super.onLoadResource(view, url);
            }

            /**
             * 步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
             * 步骤2：将该html文件放置到代码根目录的assets文件夹下
             * 步骤3：复写WebViewClient的onRecievedError方法
             * 该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
             */
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {//加载页面的服务器出现错误时调用（如404）
                switch (error.getErrorCode()) {
                    case ERROR_HOST_LOOKUP:
                        view.loadUrl("file://android_assets/error_handle.html");
                        break;

                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {//处理https处理 ,webView默认是不处理https请求的，页面显示空白，需要进行如下设置：
                handler.proceed();//表示等待证书相应
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public void setSettings() {
        settings = webView.getSettings();

        settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置允许JS弹窗

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true);//缩放至屏幕的大小

        settings.setSupportZoom(true);//支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true);//设置内置的缩放控件。若为false，则该WebView不可缩放

        //其他细节操作
        settings.setLoadsImagesAutomatically(true);//设置WebView是否应加载图像资源。请注意，此方法控制所有图像的加载，包括使用数据URI方案嵌入的图像。
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);////关闭webview中缓存
        settings.setDefaultTextEncodingName("utf-8");
    }

    public void clearCache() {
        //清除网页访问留下的缓存,由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
        webView.clearCache(true);
        //清除当前webview访问的历史记录,只会webview访问历史记录里的所有记录除了当前访问记录
        webView.clearHistory();
        //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
        webView.clearFormData();
    }

    /**
     * 辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
     */
    public void setWebChromeClient(){

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {//获得网页的加载进度并显示
               if (newProgress < 100){
                   String progress = newProgress+"%";
               }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {//获取Web页中的标题
                 //百度一下，你就知道
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {//支持javascript的警告框,返回值没有什么意义
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("title")
                        .setMessage(message)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {//支持javascript的确认框
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("JsConfirm")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;// 返回布尔值：判断点击时确认还是取消，true表示点击了确认；false表示点击了取消；
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {//支持javascript输入框,点击确认返回输入框中的值，点击取消返回 null。
                final EditText editText = new EditText(MainActivity.this);
                editText.setText(defaultValue);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(message)
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        // 只需要将第一种方法的loadUrl()换成下面该方法即可
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {

                                }
                            });
                        }else {
                            // 注意调用的JS方法名要对应上,调用javascript的callJS()方法
                            webView.loadUrl("javascript:callJS()");
                        }
                    }
                });
                break;

            case R.id.btn_second:
                startActivity(new Intent(this,SecondActivity.class));
                break;

            case R.id.btn_second_two:
                startActivity(new Intent(this,Second2Activity.class));
                break;

            case R.id.btn_second_third:
                startActivity(new Intent(this,Second3Activity.class));
                break;
        }
    }
}
