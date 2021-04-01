package jingpinwu.android.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedbackActivity extends Activity
{
    private WebView webView;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_jiexi);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在加载...");
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK)
                {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                }
                return false;
            }
        });
        webView = (WebView)findViewById(R.id.jiexi_webview);
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //支持js
        settings.setJavaScriptEnabled(true);
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setAppCacheEnabled(true);//是否使用缓存
        settings.setDomStorageEnabled(true);//DOM Storage
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        //支持获取手势焦点
        webView.requestFocusFromTouch();

        webView.loadUrl("https://shimo.im/docs/vppYvWWVydRQRC6P/");//
        TextView tv = (TextView)findViewById(R.id.toptitle_sougou);
        tv.setText("问题反馈");
        ImageView back = (ImageView)findViewById(R.id.img_sougoufanhui);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FeedbackActivity.this.finish();
            }
        });
        ImageView save = (ImageView)findViewById(R.id.img_sougousousuo);
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                webView.reload();
            }
        });
        save.setImageResource(R.drawable.save);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            //WebView不能加载https,需要重写这个方法
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                progressDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                progressDialog.dismiss();
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
