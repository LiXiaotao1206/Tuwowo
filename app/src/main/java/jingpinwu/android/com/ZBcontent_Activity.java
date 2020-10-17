package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;


import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.VideoActivity;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class ZBcontent_Activity extends Activity
{
    BetaAdapter betaAdapter;
    ListView listView;
    WebView webView;
    Document document;
    Elements elements;
    String videourl;
    String bofangurl;
    int ItemId=0;
    int current=1;
    boolean overflg = false;
    String[] url = new String[100];
    ProgressDialog progressDialog;
    List<Map<String,String>> xianluList = new ArrayList<>();
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
                    ZBcontent_Activity.this.finish();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    if(TbsVideo.canUseTbsPlayer(getApplicationContext()))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("screenMode", 102);//实现默认全屏+控制栏等UI
                        TbsVideo.openVideo(getApplicationContext(), bofangurl, bundle);
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(bofangurl), "video/*");
                        startActivity(intent);
                    }
                    break;
                case 400:
                    String[] a = new String[xianluList.size()];
                    for(byte i=0;i<xianluList.size();i++)
                    {
                        a[i] = xianluList.get(i).get("name");
                    }
                    new AlertDialog.Builder(ZBcontent_Activity.this).setItems(a, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String url = xianluList.get(which).get("url");
                            if(url.contains("leshitya.com/tv/"))//地方频道
                            {
                                bofangurl = url;
                                new Thread(runnable_difang_video).start();
                            }
                            else
                            {
                                webView.loadUrl(url);
                            }
                        }
                    }).create().show();
                    break;

                case 600:
                    webView.loadUrl(bofangurl);
                    break;
                case 700:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ZBcontent_Activity.this,"该时段暂不可播放！",Toast.LENGTH_LONG).show();
                    break;
                case 800:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ZBcontent_Activity.this,"正在使用备用线路直播！",Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(ZBcontent_Activity.this, XLVideoActivity.class);
                    intent3.putExtra("XLVIDEOURL", bofangurl);
                    intent3.putExtra("XLVIDEOFLG", "no");
                    startActivity(intent3);
                    break;
                default:
                    break;
            }
        }
    };
    Runnable runnable_xinhao = new Runnable()
    {
        @Override
        public void run()
        {
            overflg = false;
            boolean net = true;
            try
            {
                document = Jsoup.connect(bofangurl).timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select("section").select(".tab-list-syb");
                if(elements.size()==0)
                {
                    Message message = new Message();
                    message.what = 600;
                    handler.sendMessage(message);
                    return;
                }
                Elements elements_xinhao = elements.get(0).select("li");
                if(elements_xinhao.size()==0)
                {
                    Message message = new Message();
                    message.what = 600;
                    handler.sendMessage(message);
                    return;
                }
                xianluList.clear();
                for(Element e:elements_xinhao)
                {
                    Map<String,String> map = new HashMap<>();
                    map.put("name",e.text());
                    map.put("url","http://m.leshitya.com"+e.select("a").attr("href"));
                    xianluList.add(map);
                }
                if(xianluList.size()>1)
                {
                    Message message = new Message();
                    message.what = 400;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 600;
                    handler.sendMessage(message);
                }
            }
        }
    };
    Runnable runnable_difang = new Runnable()   //地方电视台需要解析出每个地方有哪些电视台
    {
        @Override
        public void run()
        {
            overflg = false;
            boolean net = true;
            try
            {
                document = Jsoup.connect(bofangurl).timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select(".tvlist");
                if(elements.size()==0)
                {
                    Message message = new Message();
                    message.what = 700;
                    handler.sendMessage(message);
                    return;
                }
                Elements elements_xinhao = elements.get(0).select("li");
                if(elements_xinhao.size()==0)
                {
                    Message message = new Message();
                    message.what = 700;
                    handler.sendMessage(message);
                    return;
                }
                xianluList.clear();
                for(Element e:elements_xinhao)
                {
                    Map<String,String> map = new HashMap<>();
                    map.put("name",e.text());
                    map.put("url","http://m.leshitya.com"+e.select("a").attr("href"));
                    xianluList.add(map);
                }
                if(xianluList.size()>1)
                {
                    Message message = new Message();
                    message.what = 400;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 600;
                    handler.sendMessage(message);
                }
            }
        }
    };
    Runnable runnable_difang_video_m3u8 = new Runnable()   //地方电视台解析m3u8
    {
        @Override
        public void run()
        {
            overflg = false;
            boolean net = true;
            try
            {
                document = Jsoup.connect(bofangurl).timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select("video");
                if(elements.size()>0)
                {
                    bofangurl = elements.get(0).attr("src");
                    Message message = new Message();
                    message.what = 300;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 700;
                    handler.sendMessage(message);
                }
            }
        }
    };
    Runnable runnable_difang_video = new Runnable()   //地方电视台解析m3u8
    {
        @Override
        public void run()
        {
            overflg = false;
            boolean net = true;
            try
            {
                document = Jsoup.connect(bofangurl).timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select("iframe");
                if(elements.size()>0)
                {
                    bofangurl = elements.get(0).attr("src");
                    new Thread(runnable_difang_video_m3u8).start();
                }
                else
                {
                    Message message = new Message();
                    message.what = 700;
                    handler.sendMessage(message);
                }
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
                document = Jsoup.connect("http://m.leshitya.com/").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.getElementsByClass("tvlist").get(current).select("li");
                String name = "";
                String dijiji = "兔喔喔TV";
                String imgurl = "live";
                if(elements!=null)
                {
                    ItemId=0;
                    for (Element e : elements)
                    {
                        name = e.text().toString();
                        Beta beta = new Beta(name,dijiji, imgurl);
                        betaList.add(beta);
                        url[ItemId++] = "http://m.leshitya.com"+ e.select("a").attr("href").toString();
                    }
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
            overflg = true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbcontent);
        webView = (WebView)findViewById(R.id.zbwebview);
        WebSettings settings;
        settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //支持js
        settings.setJavaScriptEnabled(true);
        //settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if(url.contains(".m3u8") && !url.contains(".gif"))
                {

                    bofangurl = url;
                    Message message = new Message();
                    message.what = 300;
                    handler.sendMessage(message);
                    return new WebResourceResponse(null, null, null);
                }

                return super.shouldInterceptRequest(view, url);
            }
        });
        Intent intent = getIntent();
        current = intent.getExtras().getInt("ZHIBO");
        listView = (ListView)findViewById(R.id.zbListview);
        betaAdapter = new BetaAdapter(ZBcontent_Activity.this,R.layout.betalistitem,betaList);
        new Thread(runnable).start();

        progressDialog = ProgressDialog.show(ZBcontent_Activity.this,"","加载中...",true,true,null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                bofangurl = url[i];
                //webView.loadUrl(bofangurl);
                if(bofangurl.contains("leshitya.com/tv/"))//地方台
                {
                    new Thread(runnable_difang).start();
                }
                else
                {
                    new Thread(runnable_xinhao).start();
                }
                progressDialog = ProgressDialog.show(ZBcontent_Activity.this,"","加载中...",true,true,null);
            }
        });

    }
}
