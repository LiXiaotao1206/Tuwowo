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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class JSContentActivity extends AppCompatActivity
{
    WebView webView;
    Document document = null;
    Elements elements = null;
    String url = "www.baidu.com";
    String gengxinurl="";
    int i=0,j=0,k=0;
    String[] imgstr = new String[100];
    String[] textstr = new String[500];
    ProgressDialog progressDialog;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 2)
            {
                webView.loadUrl("file:///mnt/sdcard/jingpinwu/jingpinwu.html");
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
                JSContentActivity.this.finish();
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
                elements = document.select("article");

                if(elements != null)
                {

                    //textstr[k++] = document.select(".title").get(0).text().toString();//把摘要加上

                    for (Element e : elements.select("p"))
                    {
                        textstr[k++] = "  "+e.text().toString()+ "\r\n";
                        i++;
                    }
                    for (Element e : elements.select("img"))
                    {
                        String imgurl = e.attr("data-original-src");
                        if(!imgurl.contains("http"))
                        {
                            imgurl = "https:"+imgurl;
                        }
                        imgstr[j++] = imgurl;
                    }

                    String html = "";
                    html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body><div class=\"content\"><div class=\"article_con\">";
                    String p = "";
                    int s = 10;
                    if(j!=0)
                    {
                        s = k / j;
                    }
                    p += "<p>" + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + textstr[0] + "<br><br>" + "</p>";
                    p += "<p>" + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + textstr[1] + "" + "</p>";
                    int c = 0;
                    for (int a = 2; a < i; a++)
                    {
                        if(a%s == 2)
                        {
                            if(c<=j)
                            {
                                p += "<p style=\"text-align: center;\"><img src=\"" + imgstr[c++] + "\"border=\"0\"></p>";
                            }
                        }
                        p += "<p>" + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + textstr[a] + "" + "</p>";
                    }

                    html += (p + "<p style=\"text-align: center;\">--转载请注明来源于兔喔喔--</p>");
                    html +="<p style=\"text-align: center;\"><input type=\"button\" style = \"center\" value=\"下载兔喔喔APP\" onclick=\"javascrtpt:window.location.href='"+gengxinurl+"'\"></p>";
                    html += "<p><br><br></p></div></body></html>";
                    File file = new File("/sdcard/jingpinwu/jingpinwu.html");
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
        }
    };

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_jscontent);
        Intent intent = getIntent();
        url = intent.getExtras().getString("JSURL").toString();
        gengxinurl = intent.getExtras().getString("GENGXINURL").toString();
        progressDialog = ProgressDialog.show(JSContentActivity.this,"","加载中",true);
        webView = (WebView)findViewById(R.id.jscontent_webview);
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
        setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Toast.makeText(JSContentActivity.this,"请点击屏幕右上角菜单分享给好友",Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                handler.proceed(); // 接受所有网站的证书
            }

        });
    }

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
                    copyFile(new File("/sdcard/jingpinwu/jingpinwu.html"),new File("/sdcard/jingpinwu/"+"生活随笔"+".html"));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                shareFile(JSContentActivity.this,new File("/sdcard/jingpinwu/"+"生活随笔"+".html"));
            }
            else
            {
                Toast.makeText(getApplicationContext(),"请先在主页检查版本更新！",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
