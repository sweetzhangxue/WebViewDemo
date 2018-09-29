package com.example.zx.webviewdemo;

import android.webkit.JavascriptInterface;

public class AndroidtoJs extends Object{

    @JavascriptInterface
    public void hello(String message){
        System.out.println(message);
    }
}
