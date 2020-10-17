package jingpinwu.android.com;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;



/**
 * Created by lixiaotao on 2018/8/4.
 */

public class GengxinrizhiActivity extends AppCompatActivity
{
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gengxin);
        webView = (WebView)findViewById(R.id.webview_gengxin);
        webView.loadUrl("file:///android_asset/gengxinrizhi.html");
    }

}
