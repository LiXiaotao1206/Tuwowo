package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



public class LoginActivity extends Activity
{
    WebView webView;
    WebView webView_noshow;
    String gengxinurl;
    String isgengxin;
    boolean goMainFlg;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    String lastUrl;
    int memberFlg;  //1：高级会员   0：普通会员   -1：状态异常
    String loginUrl = "http://slark.wap.dlszywz.net.cn/dom/denglu.php?username=slark&wap=1";
    String userUrl =  "http://slark.wap.dlszywz.net.cn/dom/sc_user_center.php?username=slark&wap=1";

    @Override
    protected void onResume()
    {
        goMainFlg = false;
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        Intent intent = this.getIntent();
        gengxinurl = intent.getExtras().getString("GENGXINURL");
        isgengxin = intent.getExtras().getString("GENGXINFLG");
        alertDialog = new AlertDialog.Builder(LoginActivity.this).setTitle("提示：").setMessage("您当前为\"普通会员\"，无使用权限，请发送邮件到 tuwowo@aliyun.com 或联系管理员升级为\"黄金会员\"或\"铂金会员\"！").setPositiveButton("好", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                LoginActivity.this.finish();
                System.exit(0);
            }
        }).setCancelable(false).create();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在连接服务器...");
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK)
                {
                    progressDialog.dismiss();
                }
                return false;
            }
        });
        progressDialog.show();

        webView = (WebView)findViewById(R.id.webview_login);
        WebSettings webSettings;
        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSavePassword(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; OE106 Build/OPM1.171019.026) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36");

        webView_noshow = (WebView)findViewById(R.id.webview_login_noshow) ;
        webView_noshow.getSettings().setJavaScriptEnabled(true);
        webView_noshow.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
        webView_noshow.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s)
            {
                //return super.shouldOverrideUrlLoading(webView, s);
                webView.loadUrl(s);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                //view.loadUrl("javascript:window.local_obj.showSource('<head>'+"+
                        //"document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                if(goMainFlg == false)// 不在要求权限
                {
                    goMainFlg = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("GENGXINURL", gengxinurl);
                    intent.putExtra("GENGXINFLG", isgengxin);
                    startActivity(intent);
                }
                super.onPageFinished(view, url);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.loadUrl(loginUrl);

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap)
            {
                webView.setVisibility(View.INVISIBLE);
                if(!progressDialog.isShowing() && !s.contains("channel_id"))
                {
                    progressDialog.show();
                }
                lastUrl = s;
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1)
            {
                //super.onReceivedError(webView, i, s, s1);
                Toast.makeText(LoginActivity.this,"连接服务器失败，请退出重试！",Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s)
            {
                if(s.contains("channel_id"))//兔喔喔页
                {
                    webView.loadUrl(lastUrl);
                    memberFlg = -1;
                    webView_noshow.loadUrl(s);
                }
                else if(s.contains("showWelcome"))//欢迎页
                {
                    webView.loadUrl(userUrl);
                }
                else if(s.contains("head_pic.php"))//更改头像页
                {
                    webView.loadUrl(userUrl);
                }
                else if(s.contains(".jzabc"))//ABC建站页
                {
                    webView.loadUrl(loginUrl);
                }
                else
                {
                    webView.loadUrl(s);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s)
            {
                String javascript2 = "";
                if (s.contains("user_center.php"))//用户中心
                {
                    javascript2 = "javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('Return')[0].remove();" +
                            "document.getElementsByClassName('ev_zhichi')[0].remove();" +
                            "document.getElementsByClassName('toolMenu')[0].remove();" +
                            "}";
                } else if (s.contains("denglu.php"))
                {
                    javascript2 = "javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('head_tit')[0].remove();" +
                            "document.getElementsByClassName('ev_zhichi')[0].remove();" +
                            "}";
                } else if (s.contains("channel_id"))
                {
                    javascript2 = "javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            "document.getElementsByClassName('new_mainContainer_tit')[0].remove();" +
                            "document.getElementsByClassName('ev_zhichi')[0].remove();" +
                            "document.getElementsByClassName('fixedNavBut')[0].remove();" +
                            "}";
                } else
                {
                    javascript2 = "javascript:function hideOther() {" +
                            "document.getElementsByTagName('body')[0].innerHTML;" +
                            //"document.getElementsByTagName('div')[0].style.display='none';" +
                            "document.getElementsByClassName('ev_zhichi')[0].remove();" +
                            "}";
                }
                //创建方法
                webView.loadUrl(javascript2);
                //加载方法
                webView.loadUrl("javascript:hideOther();");
                webView.setVisibility(View.VISIBLE);
                if (progressDialog.isShowing() && !s.contains("channel_id"))
                {
                    progressDialog.dismiss();
                }
            }

        });
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        new AlertDialog.Builder(this).setTitle("提示").setMessage("退出兔喔喔？").setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        })
        .setPositiveButton("退出", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                System.exit(0);
            }
        }).create().show();
    }

    final class InJavaScriptLocalObj
    {

        @JavascriptInterface
        public void showSource(String html)
        {
            if(html.contains("兔喔喔技术有限公司"))
            {
                if(goMainFlg == false)
                {
                    goMainFlg = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("GENGXINURL", gengxinurl);
                    intent.putExtra("GENGXINFLG", isgengxin);
                    startActivity(intent);
                }
            }
            else
            {
                if(alertDialog!=null && !alertDialog.isShowing())
                {
                    alertDialog.show();
                }
            }
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }

}
