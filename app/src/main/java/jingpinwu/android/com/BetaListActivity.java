package jingpinwu.android.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import  org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class BetaListActivity extends AppCompatActivity
{
    BetaAdapter betaAdapter;
    ListView listView;
    Document document_zhineng;
    Elements elements_zhineng;
    int page = 1;
    Boolean isover = true;
    String[] url = new String[100];
    int ItemId = 0;
    String leixing="";
    ProgressDialog progressDialog;
    String gengxinurl="";
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 100:
                    if(isover == true)
                    {
                        betaAdapter.notifyDataSetChanged();
                        listView.setAdapter(betaAdapter);
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
                    BetaListActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
    private List<Beta> BetaList = new ArrayList<Beta>();
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            isover = false;
            boolean net = true;
            try
            {
                if(leixing.equals("pp"))
                {
                    leixing = "people";
                }
                if(leixing.equals("ap"))
                {
                    leixing = "app";
                }
                document_zhineng = Jsoup.connect("http://"+leixing+".techweb.com.cn/list_"+page+".shtml#wp").timeout(30000).get();
            } catch (IOException e)
            {
                net = false;
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
                isover = true;

                //e.printStackTrace();
            }
            if(net == true)
            {
                elements_zhineng = document_zhineng.select(".list_con").select(".picture_text");
                String title = "";
                String text = "";
                String imgurl = "";
                if(elements_zhineng!=null)
                {
                    if(page != 1)
                    {
                        Beta beta1 = new Beta("上一页", "兔喔喔： 返回上页精彩内容", null);
                        BetaList.add(beta1);
                        url[ItemId++] = "";
                    }
                    for (Element e : elements_zhineng)
                    {
                        title = e.select(".text").select("h4").text().toString();
                        text = e.select(".text").select(".time_tag").select("span").text().toString();
                        if (text.length() > 10)
                        {
                            text = text.substring(0, 10);
                        }
                        imgurl = e.select(".picture").select("a").select("img").attr("src").toString();
                        if (title.length() > 0)
                        {
                            Beta beta = new Beta(title, "兔喔喔：" + text, imgurl);
                            BetaList.add(beta);
                            url[ItemId++] = e.select(".text").select("a").attr("href").toString();
                        }
                    }
                    Beta beta2 = new Beta("下一页", "兔喔喔： 下一页更精彩" , null);
                    BetaList.add(beta2);
                    url[ItemId++] = "";
                    Message message = new Message();
                    message.what = 100;
                    handler.sendMessage(message);
                    isover = true;
                }
                else
                {
                    Message message = new Message();
                    message.what = 200;
                    handler.sendMessage(message);
                    isover = true;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_betalist);
        Intent intent = getIntent();
        leixing = intent.getExtras().getString("LEIXING").toString();
        gengxinurl = intent.getExtras().getString("GENGXINURL").toString();
        listView = (ListView)findViewById(R.id.betalistview);
        listView.setDividerHeight(0);
        betaAdapter = new BetaAdapter(BetaListActivity.this,R.layout.betalistitem,BetaList);
        new Thread(runnable).start();
        progressDialog = ProgressDialog.show(BetaListActivity.this,"","加载中",true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if( page != 1)
                {
                    if(i == 0)
                    {
                        page--;
                        ItemId = 0;
                        BetaList.clear();
                        new Thread(runnable).start();
                        progressDialog = ProgressDialog.show(BetaListActivity.this,"","加载中",true);
                    }
                    else if(i == BetaList.size()-1)
                    {
                        page++;
                        ItemId = 0;
                        BetaList.clear();
                        new Thread(runnable).start();
                        progressDialog = ProgressDialog.show(BetaListActivity.this,"","加载中",true);

                    }
                    else
                    {
                        Intent intent = new Intent(BetaListActivity.this,JiemianActivity.class);
                        intent.putExtra("JIEMIANURL",url[i]);
                        intent.putExtra("GENGXINURL", gengxinurl);
                        startActivity(intent);
                    }
                }
                else
                {
                    if(i == BetaList.size()-1)
                    {
                        page++;
                        ItemId = 0;
                        BetaList.clear();
                        new Thread(runnable).start();
                        progressDialog = ProgressDialog.show(BetaListActivity.this,"","加载中",true);

                    }
                    else
                    {
                        Intent intent = new Intent(BetaListActivity.this,JiemianActivity.class);
                        intent.putExtra("JIEMIANURL",url[i]);
                        intent.putExtra("GENGXINURL", gengxinurl);
                        startActivity(intent);
                    }
                }

            }
        });
    }
}
