package jingpinwu.android.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lixiaotao on 2018/8/4.
 */

public class StartActivity extends AppCompatActivity
{
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int banben = 263;//以后不用再手动改这里了
    private String gengxinurl = "http://slark.ys168.com/";
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    private WebView webView;
    private Boolean goflg = false;
    private String isgengxin = "no";
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 100)
            {
                //readBanben();
                String str = webView.getTitle();
                int b = 0;
                if(webView.getUrl()!=null)
                {
                    if(str.length()>3)
                    {
                        //str = str.substring(str.length() - 3, str.length());
                        String f = str.substring(0, 1);
                        if (f.equals("0") || f.equals("1") || f.equals("2") || f.equals("3") || f.equals("4") || f.equals("5") || f.equals("6") || f.equals("7") || f.equals("8") || f.equals("9"))
                        {
                            if(str.equals("100"))
                            {
                                new AlertDialog.Builder(StartActivity.this).setTitle("提示：").setMessage("兔喔喔现在已停止服务，若您仍有此方面需求，请发送邮件至tuwowo@aliyun.com,兔喔喔开发者团队将竭诚为您服务，谢谢您的支持！").setPositiveButton("退出", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        System.exit(0);
                                    }
                                }).setCancelable(false).create().show();
                            }
                            else
                            {
                                try {
                                    b = Integer.parseInt(str);
                                }
                                catch (NumberFormatException e)
                                {
                                    b=100;
                                    Log.e("AAAA","startActivity: read banben="+str);
                                    showAlert("错误","此版本功能异常，请手动更新！","继续使用","下载新版本",true);
                                }
                                if (b > banben)//b是从webview读取到的版本号
                                {
                                    checkBanben(b);
                                } else
                                {
                                    goLogin();
                                }
                            }
                        }
                        else
                        {
                            showAlert("服务器连接失败","\r\n请联系管理员，催他更新，也请您持续关注版本更新情况！","跳过","更新",false);
                        }
                    }
                    else if (str.length() == 3)
                    {
                        String f=str.substring(0,1);
                        if(f.equals("0")||f.equals("1")||f.equals("2")||f.equals("3")||f.equals("4")||f.equals("5")||f.equals("6")||f.equals("7")||f.equals("8")||f.equals("9"))
                        {
                            if(str.equals("100"))
                            {
                                new AlertDialog.Builder(StartActivity.this).setTitle("提示：").setMessage("兔喔喔现在已停止服务，若您仍有此方面需求，请发送邮件至tuwowo@aliyun.com,兔喔喔开发者团队将竭诚为您服务,谢谢您的支持！").setPositiveButton("退出", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        System.exit(0);
                                    }
                                }).setCancelable(false).create().show();
                            }
                            else
                            {
                                try
                                {
                                    b = Integer.parseInt(str);
                                    if(b > banben)//b是从webview读取到的版本号
                                    {
                                        checkBanben(b);
                                    }
                                    else
                                    {
                                        goLogin();
                                    }
                                }
                                catch(NumberFormatException e)
                                {
                                    goLogin();
                                }
                            }
                        }
                    }
                }
                else
                {
                    new AlertDialog.Builder(StartActivity.this).setTitle("提示").setMessage("服务器连接失败！").setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            alertDialog1.dismiss();
                            System.exit(0);
                        }
                    }).create().show();
                }
            }
            else if(msg.what == 200)
            {
                alertDialog1 = new AlertDialog.Builder(StartActivity.this).setTitle("提示").setMessage("请检查网络连接！").setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        alertDialog1.dismiss();
                        System.exit(0);
                    }
                }).create();
                alertDialog1.show();

            }

            super.handleMessage(msg);
        }
    };
    private boolean cunchupermision = false;
    private PackageManager packageManager;
    private PackageInfo packageInfo;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.layout_start);

        //向存储卡写入数据6.0版本要加入这句
        ActivityCompat.requestPermissions(StartActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        File file1 = new File("/storage/emulated/0/jingpinwu");
        if (!file1.exists())
            file1.mkdir();
        File file3 = new File("/storage/emulated/0/兔喔喔");
        if (!file3.exists())
            file3.mkdir();
        File file = new File("/sdcard/jingpinwufile");
        if(!file.exists())
        {
            if(!file.mkdirs())
            {
                Toast.makeText(StartActivity.this,"请打开本应用的存储访问权限",Toast.LENGTH_LONG).show();
            }
        }
        webView = (WebView)findViewById(R.id.webview_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        WebSettings setting;
        setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setPluginState(WebSettings.PluginState.ON);
        setting.setAllowFileAccess(true);
        setting.setLoadWithOverviewMode(true);
        setting.setUseWideViewPort(true);
        setting.setDatabaseEnabled(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setDefaultTextEncodingName("UTF-8");
        setting.setDomStorageEnabled(true);
        setting.setUserAgentString("Mozilla/5.0 (Linux; Android 10; ZTE A2021 Build/QKQ1.200816.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 COVC/045429 Mobile Safari/537.36 MQQBrowser/10.9");
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.loadUrl("https://jingpinhouse.m.icoc.bz/");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if(IsNetAvilible.isNetworkAvalible(StartActivity.this))
                {
                    goflg = true;
                }
                else
                {
                    Message message = new Message();
                    message.what = 200;
                    handler.sendMessage(message);
                    goflg = false;
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(goflg == true)
                {
                    checkPermission();
                    if(cunchupermision == false)
                    {
                        ActivityCompat.requestPermissions(
                                StartActivity.this,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                        new AlertDialog.Builder(StartActivity.this)
                                .setTitle("错误")
                                .setMessage("检测到未打开本应用的存储权限，请重新允许存储权限并重启应用！")
                                .setCancelable(false)
                                .setPositiveButton("退出", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        StartActivity.this.finish();
                                        System.exit(0);
                                    }
                                })
                                .create()
                                .show();
                        webView.loadUrl("https://jingpinhouse.m.icoc.bz/");
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 100;
                        handler.sendMessage(message);
                    }
                }
                super.onPageFinished(view, url);
            }
        });


        packageManager=getApplicationContext().getPackageManager();
        try
        {
            packageInfo=packageManager.getPackageInfo(getApplicationContext().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e)
        {
            // e.printStackTrace();
        }
        banben=packageInfo.versionCode;

    }

    void checkBanben(int newbanben)
    {
        isgengxin = "yes";
        String banben_str = ""+newbanben;
        banben_str = banben_str.substring(0,banben_str.length()-2)+"."+banben_str.substring(banben_str.length()-2,banben_str.length());
        showAlert("版本更新","检测到新版本("+banben_str+")，请下载安装！","跳过","更新",false);
    }
    void goLogin()
    {
        //不在进行登录了，直接进入主页
//        Intent intent = new Intent(StartActivity.this,LoginActivity.class);
//        intent.putExtra("GENGXINURL",gengxinurl);
//        intent.putExtra("GENGXINFLG",isgengxin);
//        startActivity(intent);
//        StartActivity.this.finish();
        Intent intent = new Intent(StartActivity.this,MainActivity.class);
        intent.putExtra("GENGXINURL",gengxinurl);
        intent.putExtra("GENGXINFLG",isgengxin);
        startActivity(intent);
        StartActivity.this.finish();
    }
    public void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            cunchupermision = false;
        }
        else
        {
            cunchupermision = true;
        }
    }

    //banbenhao:如2.26
    private void showAlert(String title, String message, String positiveTip, String negativeTip, final boolean goMainFlg)
    {
        alertDialog = new AlertDialog.Builder(StartActivity.this)
                .setCancelable(false)
                .setTitle(title).setMessage(message)
                .setPositiveButton(positiveTip, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        alertDialog.dismiss();
                        if(goMainFlg)
                        {
                            Intent intent = new Intent(StartActivity.this,MainActivity.class);
                            intent.putExtra("GENGXINURL",gengxinurl);
                            intent.putExtra("GENGXINFLG",isgengxin);
                            startActivity(intent);
                            StartActivity.this.finish();
                        }
                        else {
                            goLogin();
                        }
                    }
                })
                .setNegativeButton(negativeTip, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        alertDialog.dismiss();
                        Uri uri = Uri.parse("http://slark.ys168.com/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        System.exit(0);
                    }
                })
                .create();
        alertDialog.show();
    }
}
