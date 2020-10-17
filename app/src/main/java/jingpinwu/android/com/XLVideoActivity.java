package jingpinwu.android.com;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;


import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import jingpinwu.android.com.X5.WebViewJavaScriptFunction;


public class XLVideoActivity extends Activity
{

    WebView videoWebview;
    android.webkit.WebView originalWebview;
    AlertDialog alertDialog=null;
    AlertDialog.Builder builder=null;
    View view1;
    TextView editText;
   // ProgressDialog progressDialog;
    String original_url="";
    String url="";
    String JXQZ_url = "https://api.smq1.com/?url=";
    int JXQZ_url_index = 0;
    String m3u8="";
    String add="";
    int govideo=0;
    boolean goweb2=false;
    android.webkit.WebViewClient webviewclient_ori = new android.webkit.WebViewClient()
    {
        @Override
        public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon)
        {
            goweb2=false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(android.webkit.WebView view, String surl)
        {
            if(surl.length()>5)
            {
                if (surl.substring(0,5).contains("http"))
                {
                    url = surl;
                    goweb2=true;
                }
            }
            else
            {
                Toast.makeText(XLVideoActivity.this,"缓冲失败，请重试！",Toast.LENGTH_LONG).show();
            }
            super.onPageFinished(view, surl);
        }



        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String surl)
        {
            if(surl.length()>5)
            {
                if (surl.substring(0,5).contains("http"))
                {
                    view.loadUrl(surl);
                    return super.shouldOverrideUrlLoading(view, surl);
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        @Override
        public void onReceivedSslError(android.webkit.WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error)
        {
            handler.proceed();
        }
    };
    WebViewClient webviewclient = new WebViewClient()
    {
        @Override
        public void onLoadResource(WebView webView, String s)
        {
            super.onLoadResource(webView, s);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            m3u8="";
            govideo=0;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String ssurl)
        {
            if(add.equals("add"))
            {
                String javascript2="javascript:function hideOther() {" +
                        "document.getElementsByTagName('body')[0].innerHTML;" +
                        "document.getElementsByClassName('slide')[0].remove();"+
                        "}";
                //创建方法
                videoWebview.loadUrl(javascript2);
                //加载方法
                videoWebview.loadUrl("javascript:hideOther();");
            }
            else if(add.equals("add1"))
            {
                String javascript2="javascript:function hideOther() {" +
                        "document.getElementsByTagName('body')[0].innerHTML;" +
                        "document.getElementsByClassName('tooltip')[0].remove();"+
                        "}";
                //创建方法
                videoWebview.loadUrl(javascript2);
                //加载方法
                videoWebview.loadUrl("javascript:hideOther();");
            }
            if (alertDialog!=null&&alertDialog.isShowing())
            {
                builder.setCancelable(true);
                alertDialog.dismiss();
            }
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, ssurl);
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
        public void onReceivedError(WebView webView, int i, String s, String s1)
        {
            webView.loadUrl("file:///android_asset/interneterror.html");
            super.onReceivedError(webView, i, s, s1);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String ssurl)
        {
            if(add.contains("add"))
            {
                //去除视频页面广告
                if (ssurl.contains("//p.") || ssurl.contains(".jpg")  || ssurl.contains(".png") || ssurl.contains(".ico") || ssurl.contains(".gif"))
                {
                    return new WebResourceResponse(null, null, null);
                }
            }
            return super.shouldInterceptRequest(webView, ssurl);
        }
    };
    Runnable runnablehuanchong = new Runnable()
    {
        @Override
        public void run()
        {
            JXQZ_url="";
            long start = System.currentTimeMillis();
            try
            {
                Document document = Jsoup.connect("http://qmaile.com/")
                        .timeout(5000)
                        .get();
                Elements elements = document.select("#jk");
                if(elements!=null && elements.size()>0)
                {
                    Elements eles = elements.get(0).select("option");
                    if(eles!=null && eles.size()>JXQZ_url_index)
                    {
                        JXQZ_url = eles.get(JXQZ_url_index).attr("value");
                    }
                    else if(eles.size()<=JXQZ_url_index)
                    {
                        Message message = new Message();
                        message.what = 4;
                        handler.sendMessage(message);
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            if(end-start<3000)
            {
                try
                {
                    Thread.sleep(end-start);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            if(JXQZ_url.length()>0)
            {
                int count=0;
                while(!goweb2)
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if(count>10)
                        break;
                }
                if(url.contains("http"))
                {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);
                    return;
                }
            }
            if(JXQZ_url.length()<=0)
            {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
        }
    };
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                if(alertDialog.isShowing())
                {
                    builder.setCancelable(true);
                    alertDialog.dismiss();
                }
                if(TbsVideo.canUseTbsPlayer(XLVideoActivity.this))
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("screenMode", 102);
                    TbsVideo.openVideo(XLVideoActivity.this,m3u8,bundle);
                }
                else
                {
                    Intent openVideo = new Intent(Intent.ACTION_VIEW);
                    openVideo.setDataAndType(Uri.parse(m3u8), "video/*");
                    startActivity(openVideo);
                }
                XLVideoActivity.this.finish();
            }
            else if(msg.what==2)
            {
                editText.setText("正在缓冲...");
                videoWebview.loadUrl(JXQZ_url+url);
            }
            else if(msg.what==3)
            {
                JXQZ_url_index++;
                new Thread(runnablehuanchong).start();
                editText.setText("服务器"+JXQZ_url_index+" 正在缓冲...");
            }
            else if(msg.what==4)
            {
                editText.setText("加载失败，请重试！");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy()
    {
        originalWebview.destroy();
        videoWebview.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Intent intent = this.getIntent();
        url = intent.getExtras().getString("XLVIDEOURL");
        add = getIntent().getStringExtra("XLVIDEOFLG");
        original_url = url;
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xlvideo);
        videoWebview = (WebView)findViewById(R.id.xlvideowebview);
        originalWebview = new android.webkit.WebView(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        WebSettings setting = videoWebview.getSettings();
        setting.setJavaScriptEnabled(true);
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

        android.webkit.WebSettings setting2 = originalWebview.getSettings();
        setting2.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            CookieManager.getInstance().setAcceptThirdPartyCookies(videoWebview, true);
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(originalWebview, true);
        }


        if (videoWebview.getX5WebViewExtension() != null)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            enableX5FullscreenFunc();//打开全屏播放
        }
        else
        {
            //x5内核加载失败,就不能全屏了，这时可以允许手机横屏使用安卓默认的webview横屏当全屏看
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        videoWebview.setWebViewClient(webviewclient);
        videoWebview.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        //全屏播放监听
        videoWebview.addJavascriptInterface(new WebViewJavaScriptFunction() {
            @Override
            public void onJsFunctionCalled(String tag) {
                // TODO Auto-generated method stub
            }
            @JavascriptInterface
            public void onX5ButtonClicked() {
                enableX5FullscreenFunc();
            }
            @JavascriptInterface
            public void onCustomButtonClicked() {
                disableX5FullscreenFunc();
            }
            @JavascriptInterface
            public void onLiteWndButtonClicked() {
                enableLiteWndFunc();
            }
            @JavascriptInterface
            public void onPageVideoClicked() {
                enablePageVideoFunc();
            }
        }, "Android");

        originalWebview.setWebViewClient(webviewclient_ori);
        videoWebview.setBackgroundColor(Color.BLACK);
        builder = new AlertDialog.Builder(XLVideoActivity.this).setCancelable(false).setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if(alertDialog.isShowing())
                    {
                        alertDialog.setCancelable(true);
                        alertDialog.dismiss();
                        XLVideoActivity.this.finish();
                    }
                }
                return false;
            }
        });
        view1 = View.inflate(XLVideoActivity.this,R.layout.video_progress,null);
        editText = (TextView) view1.findViewById(R.id.video_progress_text);
        editText.setText("正在查询服务器...");
        builder.setView(view1);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK)
                {
                    XLVideoActivity.this.finish();
                }
                return false;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        if(add.contains("add"))
        {
            if(add.contains("add1"))
            {
                videoWebview.setVisibility(View.INVISIBLE);
                goweb2=true;
                JXQZ_url = "";
                new Thread(runnablehuanchong).start();
            }
            else
            {
                videoWebview.setVisibility(View.INVISIBLE);
                originalWebview.loadUrl(url);
                JXQZ_url = "";
                new Thread(runnablehuanchong).start();
            }
        }
        else
        {
            videoWebview.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed()
    {
        XLVideoActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        try {
            super.onConfigurationChanged(newConfig);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                //videoWebview.setVideoFullScreen(videoWebview.getContext(),true);
                //enableX5FullscreenFunc();
            }
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                //disableX5FullscreenFunc();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*************************  X5 全屏播放 **********************************/

    // 向webview发出信息
    private void enableX5FullscreenFunc() {

        if (videoWebview.getX5WebViewExtension() != null)
        {
            Log.e("AAAA","开启X5全屏播放模式");
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            videoWebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }


    private void disableX5FullscreenFunc() {
        if (videoWebview.getX5WebViewExtension() != null)
        {
            Log.e("AAAA","恢复webkit初始状态");
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", true);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            videoWebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void enableLiteWndFunc() {
        if (videoWebview.getX5WebViewExtension() != null)
        {
            Log.e("AAAA","开启小窗模式");
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            videoWebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void enablePageVideoFunc() {
        if (videoWebview.getX5WebViewExtension() != null)
        {
            Log.e("AAAA","页面内全屏播放模式");
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            videoWebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }
}

