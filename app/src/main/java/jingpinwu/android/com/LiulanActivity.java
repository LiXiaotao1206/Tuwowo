package jingpinwu.android.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class LiulanActivity extends Activity
{
    WebSettings settings;
    ProgressDialog progressDialog;
    ImageView imageView_fanhui;
    ImageView imageView_jiexi;
    ImageView imageView_shuaxin;
    android.app.AlertDialog alertDialog;
    private String url;
    private WebView liulanWebview;

    @Override
    protected void onPause()
    {
        // 清除缓存和记录
        liulanWebview.clearCache(true);
        liulanWebview.clearHistory();
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.layout_liulan);
        imageView_fanhui = (ImageView)findViewById(R.id.img_jxfanhui);
        imageView_jiexi = (ImageView)findViewById(R.id.img_jxjiexi);
        imageView_shuaxin = (ImageView)findViewById(R.id.img_jxshuaxin);
        imageView_fanhui.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LiulanActivity.this.finish();
            }
        });
        imageView_jiexi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                url = liulanWebview.getUrl().toString();
                //if((url.contains("v_show"))||(url.contains("tudou.com/v/"))||(url.contains("iqiyi.com/v"))||(url.contains("iqiyi.com/a"))||(url.contains("mgtv.com/b"))||(url.contains("le.com/ptv"))||(url.contains("cover"))||(url.contains("sohu.com/"))||(url.contains("pptv.com/show"))||(url.contains("acfun.cn/v"))||(url.contains("yinyuetai.com/video"))||(url.contains("yy.com"))||(url.contains("bilibili.com/video"))||(url.contains("wasu.cn"))||(url.contains("163.com/movie"))||(url.contains("56.com"))||(url.contains("fun.tv/vplay"))||(url.contains("1905.com/play")))
                if((url.contains("youku.com/video/id"))||((url.contains("/cover/")))||((url.contains("iqiyi.com/v_")))||(url.contains("v_show"))||(url.contains("tudou.com/v/"))||(url.contains("iqiyi.com/v"))||(url.contains("iqiyi.com/a"))||(url.contains("mgtv.com/b"))||(url.contains("le.com/ptv"))||(url.contains("cover"))||(url.contains("sohu.com/"))||(url.contains("pptv.com/show"))||(url.contains("acfun.cn/v"))||(url.contains("yinyuetai.com/video"))||(url.contains("yy.com"))||(url.contains("bilibili.com/video"))||(url.contains("wasu.cn"))||(url.contains("163.com/movie"))||(url.contains("56.com"))||(url.contains("fun.tv/vplay"))||(url.contains("1905.com/play")))
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LiulanActivity.this);
                    View view1 = View.inflate(LiulanActivity.this,R.layout.jiexixuanze,null);
                    final ImageView imageView_ku1 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu1);
                    final ImageView imageView_ku2 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu2);
                    imageView_ku1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent1 = new Intent(LiulanActivity.this,XLVideoActivity.class);
                            intent1.putExtra("XLVIDEOURL",liulanWebview.getUrl());
                            intent1.putExtra("XLVIDEOFLG","add");
                            startActivity(intent1);
                            alertDialog.dismiss();
                        }
                    });
                    imageView_ku2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent1 = new Intent(LiulanActivity.this,XLVideoActivity.class);
                            intent1.putExtra("XLVIDEOURL",liulanWebview.getUrl());
                            intent1.putExtra("XLVIDEOFLG","add1");
                            startActivity(intent1);
                            alertDialog.dismiss();
                        }
                    });
                    builder.setView(view1);
                    alertDialog = builder.create();
                    alertDialog.show();

                }
                else if(url.contains("m.v.qq.com"))
                {
                    Toast.makeText(LiulanActivity.this,"请等待页面跳转！！！",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(LiulanActivity.this,"本页面无视频资源！！！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageView_shuaxin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                liulanWebview.reload();
            }
        });
        progressDialog = ProgressDialog.show(LiulanActivity.this,"","正在加载...",false,true);
        final Intent intent = this.getIntent();
        url = intent.getExtras().getString("SEARCH").toString();
        liulanWebview = (WebView)findViewById(R.id.liulan_webview);
        settings = liulanWebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        //{
            //settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //}
        //支持js
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        //支持获取手势焦点
        liulanWebview.requestFocusFromTouch();
        liulanWebview.loadUrl(url);
        liulanWebview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                progressDialog.setMessage("正在加载..."+newProgress+"%");
                if(newProgress==100&&progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        liulanWebview.setWebViewClient(new WebViewClient()
        {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if(url.contains("logo.png"))
                {

                    InputStream open = null;
                    try
                    {
                        open = getResources().getAssets().open("baidulogo.png");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return new WebResourceResponse("image/png","UTF-8",open);
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError)
            {
                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                sslErrorHandler.proceed();
                //super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap)
            {Log.e("AAAA","LiulanActivity:"+s);
                if(!progressDialog.isShowing())
                    progressDialog = ProgressDialog.show(LiulanActivity.this,"","正在加载...",false,true);
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(!url.contains("word="))
                {
                    String javascript2 = "javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('first-card-container')[0].remove();" +
                            "document.getElementsByClassName('ns-swipe')[0].remove();" +
                            "document.getElementsByClassName('icon-login')[0].remove();" +
                            "document.getElementsByClassName('index-banner')[0].remove();" +
                            "}";
                    //创建方法
                    liulanWebview.loadUrl(javascript2);
                    //加载方法
                    liulanWebview.loadUrl("javascript:hideOther();");
                }
                super.onPageFinished(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s)
            {
                webView.loadUrl(s);
                return super.shouldOverrideUrlLoading(webView, s);
            }

        });

    }

    @Override
    public void onBackPressed()
    {
        if(liulanWebview.canGoBack())
        {
            liulanWebview.goBack();
        }
        else
        {
            super.onBackPressed();
        }
    }
}
