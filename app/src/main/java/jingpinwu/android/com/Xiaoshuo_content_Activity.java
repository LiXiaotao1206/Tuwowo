package jingpinwu.android.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by lixiaotao on 2018/4/11.
 */

public class Xiaoshuo_content_Activity extends AppCompatActivity
{
    WebView webView;
    Document document = null;
    Elements elements = null;
    String url = "www.baidu.com";
    String gengxinurl="";
    int i=0,j=0,k=0;
    String[] textstr = new String[100];
    ProgressDialog progressDialog;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 2)
            {
                webView.loadUrl("file:///mnt/sdcard/jingpinwu/jingpinwu_xiaoshuo.html");
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
            if(msg.what == 3)
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(),"请检查网络连接！",Toast.LENGTH_SHORT).show();
                Xiaoshuo_content_Activity.this.finish();
            }
            super.handleMessage(msg);
        }
    };
    Runnable runnable = new Runnable()
    {
        boolean net = true;
        @Override
        public void run()
        {
            try
            {
                document = Jsoup.connect(url).timeout(10000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
               //e.printStackTrace();
            }
            if(net == true)
            {
                elements = document.select("#htmlmain").get(0).select("#htmlContent").get(0).select("p");

                if(elements != null)
                {
                    textstr[k++] = document.select("#htmlmain").get(0).select("#chaptertitle").get(0).text();
                    if(textstr[0]!=null)
                    {
                        if(textstr[0].contains("全书网"))
                        {
                            String s = textstr[0];
                            textstr[0] = s.substring(0,s.indexOf("全书网"))+s.substring(s.indexOf("全书网")+3,s.length());
                        }
                    }
                    for (Element e : elements)
                    {
                        textstr[k++] = e.text();
                    }

                    String html = "";
                    html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body><div class=\"content\" style=\"background-color:#b0b080;\"><div class=\"article_con\" >";
                    String p ="<p style=\"font-size:25px;color:#333333;line-height:40px;\"><b>"+ textstr[0]+"</b></p>";
                    for (int a = 1; a < k; a++)
                    {
                        p += "<p style=\"font-size:23px;color:#333333;line-height:40px;\">" + textstr[a]  + "</p>";
                    }

                    html += (p + "<p style=\"text-align: center;\" color:#dddddd>--转载请注明来源于兔喔喔--</p>");
                    html += "<p><br><br></p></div></body></html>";
                    File file = new File("/sdcard/jingpinwu/jingpinwu_xiaoshuo.html");
                    if (!file.exists())
                    {
                        try
                        {
                            file.createNewFile();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    OutputStreamWriter write = null;
                    try
                    {
                        write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    BufferedWriter writer = new BufferedWriter(write);
                    try
                    {
                        writer.write(html);
                        writer.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                }
            }
            net = false;
        }
    };

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share)
        {
            String str = getIntent().getExtras().getString("GENGXINURL").toString();
            if(str.length()>2)
            {
                try
                {
                    copyFile(new File("/sdcard/jingpinwu/jingpinwu_xiaoshuo.html"),new File("/sdcard/jingpinwu/"+textstr[0]+".html"));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                shareFile(Xiaoshuo_content_Activity.this,new File("/sdcard/jingpinwu/"+textstr[0]+".html"));
            }
            else
            {
                Toast.makeText(getApplicationContext(),"请先在主页检查版本更新！",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    // 調用系統方法分享文件
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                Uri fileURI = FileProvider.getUriForFile(context, "jingpinwu.android.com.fileprovider", file);
                share.putExtra(Intent.EXTRA_STREAM, fileURI);
            }
            else
            {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        }
        else
        {
            Toast.makeText(context,"分享失败",Toast.LENGTH_SHORT).show();
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //沉浸状态栏
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
       // {
       //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
       // }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_btliulan);


        Intent intent = getIntent();
        url = intent.getExtras().getString("BTURL").toString();
        gengxinurl = intent.getExtras().getString("GENGXINURL").toString();
        progressDialog = ProgressDialog.show(Xiaoshuo_content_Activity.this,"","加载中...",true);
        webView = (WebView)findViewById(R.id.webview_bt);
        new Thread(runnable).start();
        WebSettings setting = webView.getSettings();
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
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setBlockNetworkImage(false);//解决图片不显示
        //if(Build.VERSION.SDK_INT>=21)
          //  setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                handler.proceed(); // 接受所有网站的证书
                //super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Toast.makeText(Xiaoshuo_content_Activity.this,"请点击屏幕右上角菜单分享给好友",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }
    public void copyFile(File fromFile,File toFile) throws IOException
    {
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n=0;
        while((n=ins.read(b))!=-1){
            out.write(b, 0, n);
        }

        ins.close();
        out.close();
    }
    void WriteXiaoshuoJiluFile(String sssurl)
    {
        File file = new File("/sdcard/jingpinwufile/xiaoshuo.txt");
        if (file.exists())
        {
            OutputStreamWriter write = null;
            try
            {
                write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            BufferedWriter writer = new BufferedWriter(write);
            try
            {
                writer.write(sssurl);
                writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
