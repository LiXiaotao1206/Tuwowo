package jingpinwu.android.com;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;


/**
 * Created by 李晓涛 on 2018/2/20.
 */

public class ShuomingActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_shuoming);
        WebView webView = (WebView)findViewById(R.id.webview_sm);
        webView.loadUrl("file:///android_asset/shuoming.html");
    }
}
