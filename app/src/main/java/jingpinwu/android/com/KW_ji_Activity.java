package jingpinwu.android.com;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsVideo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class KW_ji_Activity extends AppCompatActivity
{
    BetaAdapter betaAdapter;
    ListView listView;
    WebView webView;
    Spinner spinner;
    Document document;
    Elements elements;
    String searchurl = "";
    String m3u8url;
    String videourl;
    int ItemId=0;
    int current=0;
    String[] namelist;
    String dijijilist;
    String bofangurl="";
    boolean overflg = false;
    boolean flush = true;
    String[] url = new String[2048];
    ProgressDialog progressDialog;
    String laterClickedName = null;
    String[] spinner_strings;
    ArrayList<String> spinner_arraylist = new ArrayList<String>();
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 100:
                    betaAdapter.notifyDataSetChanged();
                    listView.setAdapter(betaAdapter);
                    if(flush == true)
                    {
                        betaAdapter.clear();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(KW_ji_Activity.this, R.layout.spinnertext, spinner_strings);
                        spinner.setAdapter(adapter);
                        flush = false;
                    }
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    break;
                case 200:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(),"请检查网络连接！",Toast.LENGTH_SHORT).show();
                    KW_ji_Activity.this.finish();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    if(TbsVideo.canUseTbsPlayer(KW_ji_Activity.this))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("screenMode", 102);
                        TbsVideo.openVideo(KW_ji_Activity.this,videourl,bundle);
                    }
                    else
                    {
                        Intent openVideo = new Intent(Intent.ACTION_VIEW);
                        openVideo.setDataAndType(Uri.parse(videourl), "video/*");
                        startActivity(openVideo);
                    }
                    break;
                case 400:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    if(bofangurl==null)
                    {
                        new AlertDialog.Builder(KW_ji_Activity.this).setTitle("提示").setMessage("服务器繁忙，请稍后重试！").setPositiveButton("好", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        }).create().show();
                    }
                    else
                    {
                        bofangurl = getURLDecoderString(bofangurl);
                       /* if(!bofangurl.contains(".m3u8") && bofangurl.contains(".html"))
                        {
                            bofangurl = bofangurl.substring(bofangurl.indexOf("=http")+1,bofangurl.indexOf(".html")+5);
                            Intent intent1 = new Intent(KW_ji_Activity.this, XLVideoActivity.class);
                            intent1.putExtra("XLVIDEOURL", bofangurl);
                            intent1.putExtra("XLVIDEOFLG", "add1");
                            startActivity(intent1);
                        }
                        else
                        {
                            Intent intent1 = new Intent(KW_ji_Activity.this, XLVideoActivity.class);
                            intent1.putExtra("XLVIDEOURL", bofangurl);
                            intent1.putExtra("XLVIDEOFLG", "no");
                            startActivity(intent1);
                        }*/
                        Intent intent1 = new Intent(KW_ji_Activity.this, XLVideoActivity.class);
                        intent1.putExtra("XLVIDEOURL", bofangurl);
                        intent1.putExtra("XLVIDEOFLG", "no");
                        startActivity(intent1);
                    }
                    break;
                case 500:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(KW_ji_Activity.this).setTitle("提示").setMessage("视频资源连接失败，请稍后重试！").setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            KW_ji_Activity.this.finish();
                        }
                    }).create().show();
                    break;
                case 600:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(KW_ji_Activity.this).setTitle("提示").setMessage("该资源已被取消分享！").setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            KW_ji_Activity.this.finish();
                        }
                    }).create().show();
                    break;
                default:
                    break;
            }
        }
    };
    private List<Beta> betaList = new ArrayList<Beta>();
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            overflg = false;
            boolean net = true;
            try
            {
                document = Jsoup.connect(searchurl).header("User-Agent","Mozilla/5.0 (Linux; U; Android 8.0.0; zh-CN; MHA-AL00 Build/HUAWEIMHA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 UCBrowser/12.1.4.994 Mobile Safari/537.36").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                //播放源选择
                Elements es0 = new Elements();
                Elements ess0 = document.getElementsByClass("fed-tabs-boxs").select(".fed-part-rows");//里面保存了播放源列表（第0个）和所有播放源的集数列表（第1个开始）
                if(ess0!=null && ess0.size()>=2)
                {
                   for(Element e:ess0)
                   {
                       if(e.toString().contains("<ul class=\"fed-part-rows\">"))
                       {
                           es0.add(e);
                       }
                   }
                   if(es0.size()<2)
                   {
                       Message message = new Message();
                       message.what = 500;
                       handler.sendMessage(message);
                       overflg=true;
                       return;
                   }
                }
                else
                {
                    Message message = new Message();
                    message.what = 500;
                    handler.sendMessage(message);
                    overflg=true;
                    return;
                }

                if (es0!=null && es0.size() > current)
                {
                    //源
                    spinner_arraylist.clear();
                    for(Element e:es0.get(0).select("li"))
                    {
                        spinner_arraylist.add(e.select("a").text());
                    }
                    //每个源的集
                    Elements es1 = es0.get(current+1).select("li");
                    if(es1!=null && es1.size()>0)
                    {
                        spinner_strings = (String[]) spinner_arraylist.toArray(new String[0]);
                        String name = "";
                        String dijiji = "来源：" + spinner_strings[current];
                        String imgurl = "video";
                        dijijilist = dijiji;
                        // imgurl = document.select(".loading").attr("src").toString();
                        ArrayList<String> str = new ArrayList<String>();
                        ItemId = 0;
                        for(Element e : es1)
                        {
                            name = e.select("a").text();
                            str.add(name);
                            Beta beta = new Beta(name, dijiji, imgurl);
                            betaList.add(beta);
                            url[ItemId++] = "http://360yy.cn" + e.select("a").attr("href");
                        }
                        namelist = str.toArray(new String[]{});
                        Message message = new Message();
                        message.what = 100;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    }
                }
                else
                {
                    Message message = new Message();
                    message.what = 500;
                    handler.sendMessage(message);
                }
            }
            overflg = true;
        }
    };

    @Override
    protected void onResume()
    {
        if(laterClickedName!=null)
        {
            new AlertDialog.Builder(this)
                    .setTitle("提示：")
                    .setMessage("上次播放：" + laterClickedName)
                    .setCancelable(true)
                    .setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).create().show();
        }
        laterClickedName=null;
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xldijiji);
        webView = (WebView)findViewById(R.id.xlhuanchongwebview);
        WebSettings settings;
        settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
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
        progressDialog = new ProgressDialog(KW_ji_Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中...");
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent)
            {
                if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_BACK)
                {
                    progressDialog.dismiss();
                }
                return false;
            }
        });
        progressDialog.show();
        Intent intent = getIntent();
        searchurl = intent.getExtras().getString("DIJIJI");
        listView = (ListView)findViewById(R.id.xlListview_ji);
        betaAdapter = new BetaAdapter(KW_ji_Activity.this,R.layout.betalistitem,betaList);
        new Thread(runnable).start();
        spinner = (Spinner) super.findViewById(R.id.xlSpinner);
        String[] strings=new String[]{"兔喔喔影视"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(KW_ji_Activity.this,R.layout.spinnertext,strings);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(overflg==true)
                {
                    if(progressDialog!=null && !progressDialog.isShowing())
                        progressDialog.show();
                    current = position;
                    betaAdapter.clear();
                    new Thread(runnable).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                m3u8url = url[i];
                bofangurl="";
                webView.loadUrl(m3u8url);
                //new Thread(runnable_play).start();
                if(namelist!=null)
                {
                   laterClickedName = namelist[i];
                }
                if(progressDialog!=null && !progressDialog.isShowing())
                    progressDialog.show();
            }
        });
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if(url.contains("url=http")&&bofangurl.length()<1)
                {
                    bofangurl = url;
                    Message message = new Message();
                    message.what = 400;
                    handler.sendMessage(message);
                    return new WebResourceResponse(null, null, null);
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    //URL解码
    private String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    //URL编码
    public String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
