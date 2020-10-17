package jingpinwu.android.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class XLListActivity extends AppCompatActivity
{
    XLMovieAdapter xlMovieAdapter;
    ListView listView;
    Document document;
    Elements elements;
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
                    XLListActivity.this.finish();
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
                document = Jsoup.connect(searchurl).header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            if(net == true)
            {
                elements = document.select(".ul").select("li");
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
                            name = e.select(".namee").text().toString();
                            actor = "主演："+e.select(".actor").text().toString();
                            type = "状态："+e.select(".state").text().toString();
                            time = "年代："+e.select(".name").text().toString();
                            beizhu = "搜索：兔喔喔影视";
                            imgurl = e.select(".img").select("img").attr("src").toString();
                            XLMovie xlMovie = new XLMovie(name, actor,  type, time,beizhu, imgurl);
                            XLMovieList.add(xlMovie);
                            url[ItemId++] = "https://www.11mov.com" + e.select("a").attr("href").toString();

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
        setContentView(R.layout.activity_xlmovie);
        Intent intent = getIntent();
        searchurl = intent.getExtras().getString("SEARCHURL").toString();
        listView = (ListView)findViewById(R.id.xlListview);
        xlMovieAdapter = new XLMovieAdapter(XLListActivity.this,R.layout.xianglonglistitem,XLMovieList);
        new Thread(runnable).start();
        progressDialog = ProgressDialog.show(XLListActivity.this,"","加载中...",true,true,null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent1 = new Intent(XLListActivity.this,XLList_ji_Activity.class);
                intent1.putExtra("DIJIJI", url[i]);
                startActivity(intent1);
            }
        });
    }

}
