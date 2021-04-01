package jingpinwu.android.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiaotao on 2018/4/18.
 */

public class BTListActivity extends AppCompatActivity
{
    ListView listView ;
    BetaAdapter betaAdapter;
    List<Beta> BetaList = new ArrayList<Beta>();
    Document document;
    Elements elements;
    int page = 1;
    String[] titles = new String[100];
    String[] redu = new String[100];
    String[] url = new String[100];
    int i = 0;
    ProgressDialog progressDialog;
    boolean isover = true;
    String gengxinurl="";
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(isover == true)
            {
                betaAdapter.notifyDataSetChanged();
                listView.setAdapter(betaAdapter);
            }
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            super.handleMessage(msg);
        }
    };
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            isover = false;
            try
            {
                document = Jsoup.connect("https://m.cnbeta.com/list/latest_"+page+".htm").timeout(30000).get();
            } catch (IOException e)
            {
                e.printStackTrace();
            }




            elements = document.select(".info_list").get(0).select("li");
            String title = "";
            String text = "";
            String imgurl = "";
            if(elements!=null)
            {
                if (page != 1)
                {
                    Beta beta1 = new Beta("上一页" ,  "(第" + (page - 1) + "页)", null);
                    BetaList.add(beta1);
                    i++;
                }
                for (Element e : elements)
                {
                    title = e.select(".txt_detail").text().toString();
                    text = "发布于" + e.select(".ico_time").text().toString() + " 阅读量:" + e.select(".ico_view").text().toString();
                    //if (text.length() > 10)
                    {
                       // text = text.substring(0, 10);
                    }
                    imgurl = e.select(".txt_thumb").select("img").attr("src").toString();
                    if (title.length() > 0)
                    {
                        Beta beta = new Beta(title, "兔喔喔：" + text, imgurl);
                        BetaList.add(beta);
                        titles[i] = title;
                        redu[i] = text;
                        url[i] = e.select(".txt_thumb").select("a").attr("href").toString();
                        i++;
                    }
                }
                Beta beta2 = new Beta("下一页",  "(第" + (page + 1) + "页)", null);
                BetaList.add(beta2);
                url[i++] = "";
            }
            isover = true;
            Message message = new Message();
            message.what = 200;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bt);
        gengxinurl = getIntent().getExtras().getString("GENGXINURL").toString();
        listView = (ListView)findViewById(R.id.btListView);
        progressDialog = ProgressDialog.show(BTListActivity.this,"","加载中...",true,true,null);
        new Thread(runnable).start();
        betaAdapter = new BetaAdapter(BTListActivity.this,R.layout.betalistitem,BetaList);
        //adapter = new SimpleAdapter(BTListActivity.this,list,R.layout.listitem_noimg,new String[]{"title","redu"},new int[]{R.id.bttitle,R.id.bttext});
        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int a, long l)
            {
                if(a == 0)
                {
                    if(page!=1)
                    {
                        page--;
                        i=0;
                        BetaList.clear();
                        new Thread(runnable).start();
                        progressDialog = ProgressDialog.show(BTListActivity.this,"","加载中",true);

                    }
                    else
                    {
                        Intent intent = new Intent(BTListActivity.this,BtliulanActivity.class);
                        intent.putExtra("BTURL","https://m.cnbeta.com"+url[a]);
                        intent.putExtra("GENGXINURL",gengxinurl);
                        startActivity(intent);
                    }
                }
                else if(a == listView.getCount()-1)
                {
                    page++;
                    i=0;
                    BetaList.clear();
                    new Thread(runnable).start();
                    progressDialog = ProgressDialog.show(BTListActivity.this,"","加载中",true);

                }
                else
                {
                    Intent intent = new Intent(BTListActivity.this,BtliulanActivity.class);
                    intent.putExtra("BTURL","https://m.cnbeta.com"+url[a]);
                    intent.putExtra("GENGXINURL",gengxinurl);
                    startActivity(intent);
                }
            }
        });
        File file = new File("/sdcard/jingpinwu");
        if (!file.exists())
        {
            file.mkdir();
        }
        else
        {
            if(!file.isDirectory())
            {
                file.mkdir();
            }
        }
    }
}
