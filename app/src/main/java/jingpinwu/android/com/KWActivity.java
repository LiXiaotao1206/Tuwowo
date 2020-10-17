package jingpinwu.android.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class KWActivity extends AppCompatActivity
{
    LinearLayout tabMovie;
    LinearLayout tabTelePlay;
    LinearLayout tabAnimation;
    LinearLayout tabShow;

    LinearLayout bottomMovie;
    LinearLayout bottomTelePlay;
    LinearLayout bottomAnimation;
    LinearLayout bottomShow;

    ImageView imgReturn;
    ImageView imgSearch;
    Spinner spinner;
    TextView tvTop;
    int tabId = 2;//电视剧

    int pagelast=1000;//预定义一共1000页
    int page = 1;
    boolean isSearch = true;
    boolean isFirst = true;
    XLMovieAdapter xlMovieAdapter;
    ListView listView;
    Document document;
    Elements elements;
    String[] spinerstrings;
    String searchurl = "";
    int ItemId=0;
    String[] url = new String[200];
    ProgressDialog progressDialog;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String str = "第"+page+"页";
            String str_up = "";
            if(page==1)
            {
                str_up = "无";
            }
            else
            {
                str_up = "上一页";
            }
            spinerstrings=new String[]{str_up,str,"下一页"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(KWActivity.this,R.layout.spinnertext,spinerstrings);
            spinner.setAdapter(adapter);
            spinner.setSelection(1,true);
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
                    Toast.makeText(getApplicationContext(),"资源库修复中！",Toast.LENGTH_SHORT).show();
                    KWActivity.this.finish();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(KWActivity.this)
                            .setTitle("提示：")
                            .setMessage("对不起，小可尽力了，没找到更多资源！")
                            .setPositiveButton("好", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            }).create().show();
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
                document = Jsoup.connect(searchurl).header("User-Agent","Mozilla/5.0 (Linux; U; Android 8.1.0; zh-CN; OE106 Build/OPM1.171019.026) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 Quark/3.8.5.129 Mobile Safari/537.36").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.getElementsByClass("fed-part-case").select("dl");
                String name = "资源加载中...";
                String actor = "更新中...";
                String beizhu = "更新中...";
                String type = "更新中...";
                String time = "更新中...";
                String imgurl = "null";
                if(elements!=null)
                {
                    if(elements.size()<1)
                    {
                        Message message = new Message();
                        message.what = 300;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        ItemId=0;
                        for (Element e : elements)
                        {
                            name = e.getElementsByClass("fed-part-eone fed-font-xvi").select("a").text();
                            Elements es = e.getElementsByClass("fed-part-rows").select("li");
                            if(es.size()>=4)
                            {
                                beizhu = es.get(0).text();
                                actor = es.get(1).text();
                                type = es.get(2).text();
                                time = es.get(3).text();
                            }
                            imgurl = e.select("dt").select("a").attr("data-original");
                            XLMovie xlMovie = new XLMovie(name, actor,  type, time,beizhu, imgurl);
                            XLMovieList.add(xlMovie);
                            url[ItemId++] = "http://360yy.cn" + e.select("dt").select("a").attr("href");
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
    Runnable runnable_no_search = new Runnable()
    {
        @Override
        public void run()
        {
            boolean net = true;
            try
            {
                document = Jsoup.connect(searchurl).header("User-Agent","Mozilla/5.0 (Linux; U; Android 8.1.0; zh-CN; OE106 Build/OPM1.171019.026) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 Quark/3.8.5.129 Mobile Safari/537.36").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.getElementsByClass("fed-list-info fed-part-rows").select("li");
                String name = "资源加载中...";
                String actor = "更新中...";
                String beizhu = "更新中...";
                String type = "更新中...";
                String time = "更新中...";
                String imgurl = "null";
                if(elements!=null)
                {
                    if(elements.size()<1)
                    {
                        Message message = new Message();
                        message.what = 300;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        ItemId=0;
                        for (Element e : elements)
                        {
                            name = e.getElementsByClass("fed-list-title fed-font-xiv fed-text-center fed-text-sm-left fed-visible fed-part-eone").text();
                            time = e.getElementsByClass("fed-list-desc fed-font-xii fed-visible fed-part-eone fed-text-muted fed-hide-xs fed-show-sm-block").text();
                            beizhu = e.getElementsByClass("fed-list-remarks fed-font-xii fed-text-white fed-text-center").text();
                            type = " ";
                            actor = " ";
                            imgurl = e.getElementsByClass("fed-list-pics fed-lazy fed-part-2by3").attr("data-original");
                            XLMovie xlMovie = new XLMovie(name, actor,  type, time,beizhu, imgurl);
                            XLMovieList.add(xlMovie);
                            url[ItemId++] = "http://360yy.cn" + e.getElementsByClass("fed-list-pics fed-lazy fed-part-2by3").attr("href");
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
    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.tableMovie:
                    tableMovieClicked();
                    break;
                case R.id.tableTeleplay:
                    tableTeleplayClicked();
                    break;
                case R.id.tableAnimation:
                    tableAnimationClicked();
                    break;
                case R.id.tableShow:
                    tableShowClicked();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kw);
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.xlmovie_top);
        LinTop.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(KWActivity.this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
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
        tabMovie = (LinearLayout)findViewById(R.id.tableMovie);
        tabTelePlay = (LinearLayout)findViewById(R.id.tableTeleplay);
        tabAnimation = (LinearLayout)findViewById(R.id.tableAnimation);
        tabShow = (LinearLayout)findViewById(R.id.tableShow);

        bottomMovie = (LinearLayout)findViewById(R.id.tableMovieBottom);
        bottomTelePlay = (LinearLayout)findViewById(R.id.tableTeleplayBottom);
        bottomAnimation = (LinearLayout)findViewById(R.id.tableAnimationBottom);
        bottomShow = (LinearLayout)findViewById(R.id.tableShowBottom);

        tabMovie.setOnClickListener(clickListener);
        tabTelePlay.setOnClickListener(clickListener);
        tabAnimation.setOnClickListener(clickListener);
        tabShow.setOnClickListener(clickListener);

        imgReturn = (ImageView)findViewById(R.id.img_bangdan_return);
        imgSearch = (ImageView)findViewById(R.id.img_bangdan_search);
        imgReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                KWActivity.this.finish();
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                KWActivity.this.finish();
                Intent intent = new Intent();
                //对应BroadcastReceiver中intentFilter的action
                intent.setAction("TAB");
                intent.putExtra("TAB","SEARCH");
                //发送广播
                sendBroadcast(intent);
            }
        });
        spinner = (Spinner)findViewById(R.id.kwSpiner);
        tvTop = (TextView)findViewById(R.id.toptitle);
        spinerstrings=new String[]{"无","第1页","下一页"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinnertext,spinerstrings);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0)
                {
                    if(page!=1)
                    {
                        XLMovieList.clear();
                        page--;
                        searchurl = "http://360yy.cn/index.php/vod/search/page/" +page+"/wd/" + getIntent().getStringExtra("SEARCH") + ".html";
                        new Thread(runnable).start();

                        progressDialog .setMessage("正在加载第" + page + "页...");
                        if(progressDialog!=null && !progressDialog.isShowing())
                            progressDialog.show();

                    }
                    else
                    {
                        spinner.setSelection(1, true);
                        Toast.makeText(KWActivity.this,"当前已是首页哦！",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(position == 2)
                {
                    if(page<pagelast)
                    {
                        XLMovieList.clear();
                        page++;
                        searchurl = "http://360yy.cn/index.php/vod/search/page/" +page+"/wd/" + getIntent().getStringExtra("SEARCH") + ".html";
                        new Thread(runnable).start();

                        progressDialog .setMessage("正在加载第" + page + "页...");
                        if(progressDialog!=null && !progressDialog.isShowing())
                            progressDialog.show();

                    }
                    else
                    {
                        spinner.setSelection(1, true);
                        Toast.makeText(KWActivity.this,"当前已是最后一页了哦！",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        listView = (ListView)findViewById(R.id.xlListview);
        xlMovieAdapter = new XLMovieAdapter(KWActivity.this,R.layout.xianglonglistitem,XLMovieList);
        String intent_string = getIntent().getExtras().getString("SEARCH");
        if(!intent_string.contains("http"))
        {
            searchurl = "http://360yy.cn/index.php/vod/search/page/1/wd/" + intent_string + ".html";
            ((LinearLayout)findViewById(R.id.table)).setVisibility(View.GONE);
            tvTop.setVisibility(View.GONE);
            new Thread(runnable).start();
        }
        else
        {
            isSearch = false;
            spinner.setVisibility(View.GONE);
            searchurl = "http://360yy.cn/index.php/vod/type/id/3.html";//默认综艺
            new Thread(runnable_no_search).start();
        }
        progressDialog .setMessage("正在加载电视剧榜单...");
        progressDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent1 = new Intent(KWActivity.this,KW_ji_Activity.class);
                intent1.putExtra("DIJIJI", url[i]);
                startActivity(intent1);
            }
        });
    }

    void tableMovieClicked()
    {
        if(tabId!=1)
        {
            XLMovieList.clear();
            progressDialog.setMessage("正在加载电影榜单...");
            progressDialog.show();
            tabId = 1;
            searchurl = "http://360yy.cn/index.php/vod/type/id/1.html";
            new Thread(runnable_no_search).start();
            bottomMovie.setBackgroundColor(Color.rgb(108, 187, 254));
            bottomTelePlay.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomAnimation.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomShow.setBackgroundColor(Color.rgb(238, 238, 238));
        }
    }

    void tableTeleplayClicked()
    {
        if(tabId!=2)
        {
            XLMovieList.clear();
            progressDialog.setMessage("正在加载电视剧榜单...");
            progressDialog.show();
            tabId = 2;
            searchurl = "http://360yy.cn/index.php/vod/type/id/2.html";
            new Thread(runnable_no_search).start();
            bottomMovie.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomTelePlay.setBackgroundColor(Color.rgb(108, 187, 254));
            bottomAnimation.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomShow.setBackgroundColor(Color.rgb(238, 238, 238));
        }
    }

    void tableAnimationClicked()
    {
        if(tabId!=3)
        {
            XLMovieList.clear();
            progressDialog.setMessage("正在加载动漫榜单...");
            progressDialog.show();
            tabId = 3;
            searchurl = "http://360yy.cn/index.php/vod/type/id/4.html";
            new Thread(runnable_no_search).start();
            bottomMovie.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomTelePlay.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomAnimation.setBackgroundColor(Color.rgb(108, 187, 254));
            bottomShow.setBackgroundColor(Color.rgb(238, 238, 238));
        }
    }

    void tableShowClicked()
    {
        if(tabId!=4)
        {
            XLMovieList.clear();
            progressDialog.setMessage("正在加载综艺榜单...");
            progressDialog.show();
            tabId = 4;
            searchurl = "http://360yy.cn/index.php/vod/type/id/3.html";
            new Thread(runnable_no_search).start();
            bottomMovie.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomTelePlay.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomAnimation.setBackgroundColor(Color.rgb(238, 238, 238));
            bottomShow.setBackgroundColor(Color.rgb(108, 187, 254));
        }
    }

}
