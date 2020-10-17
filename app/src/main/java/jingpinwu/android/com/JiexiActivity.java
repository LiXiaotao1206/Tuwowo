package jingpinwu.android.com;


import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class JiexiActivity extends AppCompatActivity
{
    static boolean isFirstLoad = true;
    WebSettings settings;
    ProgressDialog progressDialog;
    ImageView imageView_fanhui;
    ImageView imageView_sousuo;
    AlertDialog alertDialog = null;
    android.app.AlertDialog.Builder builder=null;
    View view1=null;
    String gourl="";
    private String url;
    private String laterji="";
    private WebView readWebview;

    @Override
    protected void onStop()
    {
        if(progressDialog.isShowing())
        {
            progressDialog.setCancelable(true);
            progressDialog.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        if(laterji!=null && laterji.length()>0)
        {
            new AlertDialog.Builder(this).setMessage("上次播放：第"+laterji+"集").setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            }).create().show();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.layout_jiexi);
        imageView_fanhui = (ImageView) findViewById(R.id.img_sougoufanhui);
        imageView_sousuo = (ImageView) findViewById(R.id.img_sougousousuo);

        final Intent intent = this.getIntent();
        url = intent.getExtras().getString("SEARCH").toString();
        progressDialog = new ProgressDialog(JiexiActivity.this);
        progressDialog.setMessage("正在加载...");
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                        JiexiActivity.this.finish();
                    }
                }
                return false;
            }
        });
        if(isFirstLoad)
        {
            isFirstLoad = false;

            new AlertDialog.Builder(JiexiActivity.this).setTitle("提示").setMessage("所有付费内容均可免费播放。\r\n资源来源于网络，非兔喔喔所有。").setPositiveButton("好", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            }).create();
        }
        builder = new android.app.AlertDialog.Builder(JiexiActivity.this);
        view1 = View.inflate(JiexiActivity.this,R.layout.jiexixuanze,null);
        final ImageView imageView_ku1 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu1);
        final ImageView imageView_ku2 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu2);
        imageView_ku1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(JiexiActivity.this,XLVideoActivity.class);
                intent1.putExtra("XLVIDEOURL",gourl);
                intent1.putExtra("XLVIDEOFLG","add1");
                startActivity(intent1);
                alertDialog.dismiss();
            }
        });
        imageView_ku2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(JiexiActivity.this,XLVideoActivity.class);
                intent1.putExtra("XLVIDEOURL",gourl);
                intent1.putExtra("XLVIDEOFLG","add");
                startActivity(intent1);
                alertDialog.dismiss();
            }
        });
        builder.setView(view1);
        alertDialog = builder.create();

        readWebview = (WebView)findViewById(R.id.jiexi_webview);
        settings = readWebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //支持js
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Mobile Safari/537.36");//电脑版搜狗浏览器
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        //支持获取手势焦点
        readWebview.requestFocusFromTouch();
        readWebview.loadUrl(url);
        readWebview.setVisibility(View.GONE);
        imageView_fanhui.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                JiexiActivity.this.finish();
            }
        });
        imageView_sousuo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                JiexiActivity.this.finish();
                Intent intent = new Intent();
                //对应BroadcastReceiver中intentFilter的action
                intent.setAction("TAB");
                intent.putExtra("TAB","SEARCH");
                //发送广播
                sendBroadcast(intent);
            }
        });
        readWebview.setWebViewClient(new WebViewClient()
        {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if(url.contains("/images/logo-video-2"))
                {

                    InputStream open = null;
                    try {
                        open = getResources().getAssets().open("sougou.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new WebResourceResponse("image/png","UTF-8",open);
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if(!progressDialog.isShowing() && !alertDialog.isShowing())
                {
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
                view.setVisibility(View.INVISIBLE);
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                view.loadUrl("file:///android_asset/interneterror.html");
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                //编写 javaScript方法
                if(url.contains("/list/"))
                {
                    String javascript2 ="javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByTagName('div')[0].style.display='none';" +
                            "document.getElementsByClassName('sapp-logo-wrap')[0].remove();"+
                            "document.getElementsByClassName('footer-ad')[0].remove();"+
                            "}";
                    //创建方法
                    readWebview.loadUrl(javascript2);
                    //加载方法
                    readWebview.loadUrl("javascript:hideOther();");
                }
                if(url.contains("/videoclips/"))
                {
                    String javascript2 ="javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByTagName('div')[0].style.display='none';" +
                            "document.getElementsByClassName('clips-recommend')[0].remove();"+
                            "document.getElementsByClassName('footer-ad')[0].remove();"+
                            "}";
                    //创建方法
                    readWebview.loadUrl(javascript2);
                    //加载方法
                    readWebview.loadUrl("javascript:hideOther();");
                }
                if(url.contains("v?query"))
                {
                    String javascript2 ="javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByTagName('div')[0].remove();" +
                            "document.getElementsByClassName('footer')[0].remove();"+
                            "document.getElementsByTagName('section')[0].remove();"+
                            "}";
                    //创建方法
                    readWebview.loadUrl(javascript2);
                    //加载方法
                    readWebview.loadUrl("javascript:hideOther();");
                }
                if(url.contains("=teleplay")||url.contains("=film")||url.contains("=tvshow")||url.contains("=cartoon"))
                {
                    String javascript ="javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('top-header-black')[0].remove();"+
                            "document.getElementsByClassName('module dt-relative')[0].remove();"+
                            "document.getElementsByClassName('module dt-like')[0].remove();"+
                            "document.getElementsByClassName('dt-rec')[0].remove();"+
                            "document.getElementsByClassName('wap-footer')[0].remove();"+
                            "document.getElementsByClassName('module actors')[0].remove();"+
                            //"document.getElementsByClassName('module dt-playlist')[0].remove();"+
                            "document.getElementsByClassName('module dt-videofeed')[0].remove();"+
                            "}";
                    //创建方法
                    readWebview.loadUrl(javascript);
                    //加载方法
                    readWebview.loadUrl("javascript:hideOther();");
                    view.setVisibility(View.VISIBLE);
                }
                else if(url.contains("teleplay")||url.contains("movie")||url.contains("tvshow")||url.contains("cartoon"))
                {
                    String javascript ="javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('dt-header')[0].remove();"+
                            "document.getElementsByClassName('module dt-relative')[0].remove();"+
                            "document.getElementsByClassName('module dt-like')[0].remove();"+
                            "document.getElementsByClassName('dt-rec')[0].remove();"+
                            "document.getElementsByClassName('wap-footer')[0].remove();"+
                            "document.getElementsByClassName('module actors')[0].remove();"+
                            //"document.getElementsByClassName('module dt-playlist')[0].remove();"+
                            "document.getElementsByClassName('module dt-videofeed')[0].remove();"+
                            "}";
                    //创建方法
                    readWebview.loadUrl(javascript);
                    //加载方法
                    readWebview.loadUrl("javascript:hideOther();");
                    view.setVisibility(View.VISIBLE);
                }
                if (progressDialog.isShowing())
                {
                    progressDialog.setCancelable(true);
                    progressDialog.dismiss();
                }
                readWebview.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view,  String ssurl)
            {
                if(ssurl.contains("m.sogou.com"))
                {
                    return true;
                }
                if((!ssurl.contains("=film"))&&(!ssurl.contains("=teleplay"))&&(!ssurl.contains("=cartoon"))&&(!ssurl.contains("=tvshow"))
                    &&(!ssurl.contains("/film/"))&&(!ssurl.contains("/teleplay/"))&&(!ssurl.contains("/cartoon/"))&&(!ssurl.contains("/tvshow/")))
                {
                    gourl = ssurl;
                    String cururl = readWebview.getUrl();
                    laterji=null;
                    if(cururl.contains("&j=") && cururl.contains("&st="))
                    {
                        laterji = cururl.substring(cururl.indexOf("&j=")+3,cururl.indexOf("&st="));
                    }
                    Intent intent1 = new Intent(JiexiActivity.this, XLVideoActivity.class);
                    intent1.putExtra("XLVIDEOURL", gourl);
                    intent1.putExtra("XLVIDEOFLG", "add");
                    startActivity(intent1);
                    readWebview.goBack();
                    return true;
                }
                else
                {
                    readWebview.loadUrl(ssurl);
                    return false;
                }
                //return super.shouldOverrideUrlLoading(view, ssurl);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if(readWebview.canGoBack()) {
            readWebview.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
