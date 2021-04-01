package jingpinwu.android.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;



public class MikutapActivity extends Activity {

    WebView mikuWebView;
    ProgressDialog progressDialog;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mikutap);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载...");
        progressDialog.setCancelable(false);

        mikuWebView = (WebView)findViewById(R.id.miku_webview);
        WebSettings setting = mikuWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setDomStorageEnabled(true);
        setting.setUseWideViewPort(true); // 关键点
        setting.setAllowFileAccess(true); // 允许访问文件
        setting.setPluginState(WebSettings.PluginState.ON);
        setting.setLoadWithOverviewMode(true);
        setting.setDatabaseEnabled(true);
        setting.setAppCacheEnabled(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setDefaultTextEncodingName("UTF-8");
        setting.setDomStorageEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);

        setting.setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36");
        //自适应屏幕
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDisplayZoomControls(false);  //隐藏缩放按钮
        mikuWebView.loadUrl("https://aidn.jp/mikutap/");

        mikuWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, com.tencent.smtt.export.external.interfaces.WebResourceRequest webResourceRequest) {
                webView.loadUrl("https://aidn.jp/mikutap/");
                return false;
                //return super.shouldOverrideUrlLoading(webView, webResourceRequest);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                if(!progressDialog.isShowing())
                {
                    progressDialog.show();
                }
                webView.setVisibility(View.GONE);
                super.onPageStarted(webView, url, bitmap);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                //编写 javaScript方法
                String javascript2 ="javascript:function hideOther() {" +
                        "document.getElementsByTagName('body')[0].innerHTML;" +
                        "document.getElementById('bt_back').remove();"+
                        "document.getElementById('bt_fs').remove();"+
                        "document.getElementById('bt_about').remove();"+
                        "document.getElementsByClassName('tit')[0].remove();"+
                        "document.getElementsByClassName('con')[0].remove();"+
                        "}";
                //创建方法
                webView.loadUrl(javascript2);
                //加载方法
                webView.loadUrl("javascript:hideOther();");
                webView.setVisibility(View.VISIBLE);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView webView, com.tencent.smtt.export.external.interfaces.WebResourceRequest webResourceRequest, com.tencent.smtt.export.external.interfaces.WebResourceError webResourceError) {
                webView.loadUrl("file:///android_asset/interneterror.html");
                super.onReceivedError(webView, webResourceRequest, webResourceError);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        super.onBackPressed();
    }
}
