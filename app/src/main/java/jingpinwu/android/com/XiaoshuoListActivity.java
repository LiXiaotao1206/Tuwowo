package jingpinwu.android.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class XiaoshuoListActivity extends AppCompatActivity
{
    XLMovieAdapter xlMovieAdapter;
    ListView listView;
    WebView webView;
    Document document;
    Elements elements;
    TextView toptile ;
    ImageView imageView_fanhui;
    ImageView imageView_sousuo;
    String jiluurl="";
    String searchurl;
    int ItemId=0;
    String[] url = new String[100];
    ProgressDialog progressDialog;
    AlertDialog alertDialog_tishi;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 100:
                    xlMovieAdapter.notifyDataSetChanged();
                    listView.setAdapter(xlMovieAdapter);
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
                    new AlertDialog.Builder(XiaoshuoListActivity.this).setTitle("提示").setMessage("小说服务器连接失败，若您正在使用移动数据流量，请切换到联通、电信或WIFI网络重试！").setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            XiaoshuoListActivity.this.finish();
                        }
                    }).create().show();

                    break;
                case 300:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    if(jiluurl!=null && jiluurl.contains("http") && jiluurl.indexOf("http")>0)
                    {

                        alertDialog_tishi = new AlertDialog.Builder(XiaoshuoListActivity.this)
                                .setTitle("上次阅读：")
                                .setMessage(jiluurl.substring(0,jiluurl.indexOf("http")-1)+" ]")
                                .setCancelable(false)
                                .setNegativeButton("继续上次阅读", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent intent = new Intent(XiaoshuoListActivity.this,Xiaoshuo_zhangjie_List_Activity.class);
                                        intent.putExtra("DIJIJI",jiluurl.substring(jiluurl.indexOf("http")));
                                        intent.putExtra("CONTINUE","yes");
                                        startActivity(intent);
                                    }
                                })
                                .setPositiveButton("阅读新内容", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(!alertDialog.isShowing())
                                        {
                                            alertDialog.show();
                                        }
                                        new Thread(runnable).start();
                                    }
                                })
                                .create();
                        alertDialog_tishi.show();

                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<XLMovie> XLMovieList = new ArrayList<XLMovie>();
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            boolean net = true;
            try
            {
                document = Jsoup.connect(searchurl).ignoreContentType(true).userAgent("Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; OE106 Build/OPM1.171019.026) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/9.0 Mobile Safari/537.36").timeout(30000).get();
            } catch (IOException e)
            {
                //e.printStackTrace();
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select(".book_list").select("li");
                String name = "无搜索结果";
                String actor = "";
                String beizhu = "";
                String type = "";
                String time = "";
                String imgurl = "null";

                if(elements!=null)
                {
                    if(elements.size()<1)
                    {
                        XLMovie xlMovie = new XLMovie(name,beizhu,type,actor,time, imgurl);
                        XLMovieList.add(xlMovie);
                    }
                    else
                    {
                        ItemId=0;
                        for (Element e : elements)
                        {
                            int all = e.select("p").size();
                            name = e.select(".book_title").text().toString();
                            if(all>=2)
                                actor = e.select("p").get(0).text().toString();
                            if(all>=3)
                                beizhu = e.select("p").get(1).text().toString();
                            if(all>=4)
                                type = e.select("p").get(2).text().toString();
                            //time = e.select("p").get(4).text().toString();
                            imgurl = e.select("a").select("img").attr("src").toString();
                            XLMovie xlMovie = new XLMovie(name, beizhu, type, actor, time, imgurl);
                            XLMovieList.add(xlMovie);
                            url[ItemId++] = "http://m.quanshuwang.com" + e.select("a").get(0).attr("href").toString();

                        }
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
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xiaoshuo);
        webView = (WebView)findViewById(R.id.webview_xiaoshuo);

        try
        {
            searchurl = "http://m.quanshuwang.com/modules/article/search.php?searchkey=" + java.net.URLEncoder.encode("红楼梦", "GBK")+ "&type=";
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

        webView.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉
        webView.loadUrl(searchurl);

        readXiaoshuoJiluFile();
        toptile = (TextView)findViewById(R.id.toptitle_sougou);
        toptile.setText("兔喔喔书库");
        imageView_fanhui = (ImageView)findViewById(R.id.img_sougoufanhui) ;
        imageView_sousuo = (ImageView)findViewById(R.id.img_sougousousuo) ;
        listView = (ListView)findViewById(R.id.listview_xiaoshuo);
        xlMovieAdapter = new XLMovieAdapter(XiaoshuoListActivity.this,R.layout.xianglonglistitem,XLMovieList);

        builder =  new AlertDialog.Builder(XiaoshuoListActivity.this);
        View view1 = View.inflate(XiaoshuoListActivity.this,R.layout.xiaoshuo_sousuo,null);
        final EditText editText = (EditText)view1.findViewById(R.id.edit_xiaoshuo_sousuo);
        final Button button = (Button) view1.findViewById(R.id.btn_xiaoshuo_sousuo);
        builder.setView(view1);
        builder.setCancelable(false);
        alertDialog= builder.create();
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                builder.setCancelable(true);
                String s="";
                if(editText.getText().length()<1)
                {
                    Toast.makeText(XiaoshuoListActivity.this,"请输入书籍名称！",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        s = "http://m.quanshuwang.com/modules/article/search.php?searchkey=" + java.net.URLEncoder.encode(editText.getText().toString() , "GBK")+ "&type=";
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    if((xlMovieAdapter!=null) && (!xlMovieAdapter.isEmpty()))
                    {
                        xlMovieAdapter.clear();
                    }
                    searchurl = s;
                    webView.loadUrl(searchurl);
                    if(alertDialog.isShowing())
                    {
                        alertDialog.dismiss();
                        progressDialog.show();
                    }
                }
            }
        });
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    builder.setCancelable(true);
                    alertDialog.dismiss();
                    return true;
                }
                else
                {
                    return false; //默认返回 false
                }
            }
        });
        progressDialog = ProgressDialog.show(XiaoshuoListActivity.this,"","加载中...",true,true,null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent1 = new Intent(XiaoshuoListActivity.this,Xiaoshuo_zhangjie_List_Activity.class);
                intent1.putExtra("DIJIJI", url[i]);
                intent1.putExtra("CONTINUE", "no");
                startActivity(intent1);
            }
        });
        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageFinished(WebView webView, String s)
            {
                if(s.contains("quanshuwang.com/book_"))
                {
                    Toast.makeText(XiaoshuoListActivity.this,"只搜索到一个结果，已自动为您打开目录！",Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(XiaoshuoListActivity.this,Xiaoshuo_zhangjie_List_Activity.class);
                    intent1.putExtra("DIJIJI", s);
                    intent1.putExtra("CONTINUE", "no");
                    startActivity(intent1);
                    if((progressDialog!=null)&&(progressDialog.isShowing()))
                    {
                        progressDialog.dismiss();
                    }
                    try
                    {
                        searchurl = "http://m.quanshuwang.com/modules/article/search.php?searchkey=" + java.net.URLEncoder.encode("红楼梦", "GBK")+ "&type=";
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                    new Thread(runnable).start();
                }
                else
                {
                    new Thread(runnable).start();
                }
                super.onPageFinished(webView, s);
            }
        });
        imageView_fanhui.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                XiaoshuoListActivity.this.finish();
            }
        });
        imageView_sousuo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.show();
            }
        });


    }

    @Override
    protected void onDestroy()
    {
        if(alertDialog!=null && alertDialog.isShowing())
        {
            alertDialog.cancel();
        }
        if(alertDialog_tishi!=null && alertDialog_tishi.isShowing())
        {
            alertDialog_tishi.cancel();
        }
        super.onDestroy();
    }

    void readXiaoshuoJiluFile()
    {
        ///////////////文件
        File file = new File("/sdcard/jingpinwufile/xiaoshuo.txt");
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
        else
        {
            InputStreamReader reader = null; // 建立一个输入流对象reader
            try
            {
                reader = new InputStreamReader(
                        new FileInputStream(file));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            try
            {
                line = br.readLine();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (line!=null && line.length() > 10 && line.contains("http"))
            {
                jiluurl = line;
                Message message = new Message();
                message.what = 300;
                handler.sendMessage(message);
            }
        }
    }
}
