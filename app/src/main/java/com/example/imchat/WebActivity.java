package com.example.imchat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLDecoder;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webview_info = findViewById(R.id.webview_info);
        WebSettings setting = webview_info.getSettings();
        setting.setDomStorageEnabled(true);
        setting.setJavaScriptEnabled(true);
        setting.setDefaultTextEncodingName("utf-8"); // 设置文本编码
        setting.setAppCacheEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);// 设置缓存模式</span>
//        webview_info.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//        });
        webview_info.setDownloadListener(new MyDown());
        // 设置允许加载混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview_info.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 允许所有SSL证书
        webview_info.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String decoded_url = url;
                try {
                    decoded_url = URLDecoder.decode(url, "UTF-8");
                } catch (Exception e) {
                }
                if (!checkUrlValid(decoded_url)) {
                    super.shouldOverrideUrlLoading(view,url);
                    return false;
                } else {
                    // Do your special things
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!checkUrlValid(url)) {
                    super.onPageStarted(view,url,favicon);
                    return;
                } else {
                    // Do your special things
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!checkUrlValid(url)) {
                    super.onPageFinished(view,url);
                    return;
                } else {
                    // Do your special things
                }
            }
        });
        webview_info.loadUrl("https://item.taobao.com/item.htm?spm=a1z09.8149145.0.0.541371daDaUTQ0&id=587653844139&_u=a201ijlgh164f5");
    }

    private boolean checkUrlValid(String aUrl) {
        boolean result = true;
        if (aUrl == null || aUrl.equals("") || !aUrl.contains("http")) {
            return false;
        }
        if (aUrl.contains("s.click")) {
            result = false;
        }
        return result;
    }

    private class MyDown implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        }
    }
}
