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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Created by lixiaotao on 2018/4/10.
 */

public class XLList_ji_Activity extends AppCompatActivity
{
    BetaAdapter betaAdapter;
    ListView listView;
    WebView webView;
    Spinner spinner;
    Document document;
    Elements elements;
    String searchurl = "";
    String videourl;
    int ItemId=0;
    int count=1;
    int current=0;
    String[] namelist;
    String dijijilist;
    String bofangurl="";
    boolean overflg = false;
    boolean flush = true;
    String[] url = new String[200];
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(XLList_ji_Activity.this, R.layout.spinnertext, spinner_strings);
                        spinner.setAdapter(adapter);
                        /*
                        if (count == 1)
                        {
                            String[] strings = new String[]{"播放源一"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(XLList_ji_Activity.this, R.layout.spinnertext, strings);
                            spinner.setAdapter(adapter);
                        }
                        else if (count == 2)
                        {
                            String[] strings = {"播放源一", "播放源二"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(XLList_ji_Activity.this, R.layout.spinnertext, strings);
                            spinner.setAdapter(adapter);
                        }
                        */
                        flush = false;
                    }
                    spinner.setSelection(current,true);
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
                    XLList_ji_Activity.this.finish();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    if(TbsVideo.canUseTbsPlayer(XLList_ji_Activity.this))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("screenMode", 102);
                        TbsVideo.openVideo(XLList_ji_Activity.this,videourl,bundle);
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
                    Intent intent1 = new Intent(XLList_ji_Activity.this,XLVideoActivity.class);
                    intent1.putExtra("XLVIDEOURL",bofangurl);
                    intent1.putExtra("XLVIDEOFLG","no");
                    startActivity(intent1);
                    break;
                case 500:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(XLList_ji_Activity.this).setTitle("提示").setMessage("视频资源连接失败，请稍后重试！").setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            XLList_ji_Activity.this.finish();
                        }
                    }).create().show();
                    break;
                case 600:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(XLList_ji_Activity.this).setTitle("提示").setMessage("该资源已被取消分享！").setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            XLList_ji_Activity.this.finish();
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
                document = Jsoup.connect(searchurl).header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.getElementsByClass("playul");
                count = elements.size();
                if(count<1)
                {
                    Message message = new Message();
                    message.what = 500;
                    handler.sendMessage(message);
                }
                else
                {
                    String leixing;
                    leixing = document.select(".pinfo").get(0).select(".info").get(0).text();
                    //if(leixing.contains("类型：伦理片")||leixing.contains("分类：微拍福利"))
                    if(leixing.contains("类型：这次就取消掉了伦理片"))//类型：这次就取消掉了伦理片
                    {
                        Message message = new Message();
                        message.what = 600;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        String zongjishu = document.select(".nickname").get(0).text();
                        if(zongjishu.contains("状态：共"))
                        {
                            zongjishu+="  已全部更新";
                        }
                        Elements es = document.select(".f");
                        if (es.size() > 0)
                        {
                            for (Element e : es)
                            {
                                spinner_arraylist.add(e.text().toString());
                            }
                            spinner_strings = (String[]) spinner_arraylist.toArray(new String[0]);
                        }

                        String name = "";
                        String dijiji = "";
                        String imgurl = "video";
                        dijiji = document.select(".pic").select(".img").select("img").attr("alt").toString()+"    【 "+zongjishu+" 】";
                        dijijilist = dijiji ;
                        //imgurl = document.select(".pic").select(".img").select("img").attr("src").toString();
                        if (elements != null)
                        {
                            ArrayList<String> str = new ArrayList<String>();
                            ItemId = 0;
                            for (Element e : elements.get(current).select("li"))
                            {
                                name = e.select("a").text().toString();
                                str.add(name);
                                Beta beta = new Beta(name, dijiji, imgurl);
                                betaList.add(beta);
                                url[ItemId++] = "https://www.11mov.com" + e.select("a").attr("href").toString();
                            }
                            namelist = str.toArray(new String[]{});
                            Message message = new Message();
                            message.what = 100;
                            handler.sendMessage(message);
                        } else
                        {
                            Message message = new Message();
                            message.what = 200;
                            handler.sendMessage(message);
                        }
                    }
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
        Intent intent = getIntent();
        searchurl = intent.getExtras().getString("DIJIJI").toString();
        listView = (ListView)findViewById(R.id.xlListview_ji);
        betaAdapter = new BetaAdapter(XLList_ji_Activity.this,R.layout.betalistitem,betaList);
        new Thread(runnable).start();
        spinner = (Spinner) super.findViewById(R.id.xlSpinner);
        String[] strings=new String[]{"兔喔喔影视"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(XLList_ji_Activity.this,R.layout.spinnertext,strings);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(overflg==true)
                {
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
        progressDialog = ProgressDialog.show(XLList_ji_Activity.this,"","加载中...",true,true,null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                webView.loadUrl(url[i]);
               if(namelist!=null)
               {
                   laterClickedName = namelist[i];
               }
                progressDialog = ProgressDialog.show(XLList_ji_Activity.this,"","加载中...",true,true,null);
            }
        });
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if(url.contains(".m3u8"))
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

    void Download(String url,String name)
    {
        File file = new File("/storage/emulated/0/兔喔喔");
        if (!file.exists())
        {
            file.mkdirs();
        } else
        {
            Vibrator vibrator = (Vibrator) XLList_ji_Activity.this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(600);
            Uri uritishi = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uritishi);
            rt.play();
            Toast.makeText(getApplicationContext(), "即将正在下载到 兔喔喔 文件夹", Toast.LENGTH_LONG).show();
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(true);
            if (name.length() < 2)
            {
                name = simpleDateFormat.format(date);
                request.setTitle(simpleDateFormat.format(date));
            } else
            {
                request.setTitle(name);
            }
            request.setDescription("正在下载至   兔喔喔   文件夹");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir("兔喔喔", name + ".mp4");
            downloadManager.enqueue(request);
            name = "";
        }
    }
}
