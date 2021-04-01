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
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaotao on 2018/4/18.
 */

public class Xiaoshuo_zhangjie_List_Activity extends AppCompatActivity
{
    ListView listView ;
    BetaAdapter betaAdapter;
    List<Beta> BetaList = new ArrayList<Beta>();
    Document document;
    Elements elements;
    int page = 1;
    String[] titles = new String[500];
    String[] redu = new String[500];
    String[] url = new String[500];
    String mainurl="";
    int i = 0;
    ProgressDialog progressDialog;
    boolean isover = true;
    String gengxinurl="";
    ArrayList<String> page_select_list = new ArrayList<String>();
    String[] string_page_select_list = null;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==200)
            {
                if (isover == true)
                {
                    betaAdapter.notifyDataSetChanged();
                    listView.setAdapter(betaAdapter);
                }
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
            if(msg.what == 300)
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(),"请检查网络连接！",Toast.LENGTH_SHORT).show();
                Xiaoshuo_zhangjie_List_Activity.this.finish();
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
                document = Jsoup.connect(mainurl).timeout(30000).get();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if(string_page_select_list==null)
            {
                Elements es = document.select(".pageselectlist").get(0).select("option");
                for(Element e : es)
                {
                    page_select_list.add("http://m.quanshuwang.com"+e.attr("value"));
                }
                string_page_select_list = (String[])page_select_list.toArray(new String[page_select_list.size()]);
            }
            elements = document.select("#alllist").get(0).select("li");
            String title = "";
            String text = "";
            String imgurl = "book";
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
                    title = e.select("a").attr("title").toString();
                    text =document.select(".top").get(0).text()+" - "+e.select("a").text();
                    if(text.contains("目录"))
                    {
                        text=text.substring(0,text.indexOf("目录")-1)+" - "+text.substring(text.indexOf("目录")+2);
                    }
                    //imgurl = document.select(".fn-clear").select(".book_cover").attr("src");
                    if (title.length() > 0)
                    {
                        Beta beta = new Beta(title,  text, imgurl);
                        BetaList.add(beta);
                        titles[i] = title;
                        redu[i] = text;
                        url[i] = "http://m.quanshuwang.com"+e.select("a").attr("href").toString();
                        i++;
                    }
                }
                if((string_page_select_list!=null) && (page<string_page_select_list.length))
                {
                    Beta beta2 = new Beta("下一页", "(第" + (page + 1) + "页)", null);
                    BetaList.add(beta2);
                    url[i++] = "";
                }
            }
            else
            {
                Message message = new Message();
                message.what = 300;
                handler.sendMessage(message);
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
        mainurl = getIntent().getExtras().getString("DIJIJI").toString();
        if(!getIntent().getExtras().getString("CONTINUE").toString().contains("yes"))
        {
            mainurl = "http://m.quanshuwang.com/list/" + mainurl.substring(mainurl.indexOf("book_") + 5, mainurl.length() - 5) + "_1.html";
        }
        listView = (ListView)findViewById(R.id.btListView);
        progressDialog = ProgressDialog.show(Xiaoshuo_zhangjie_List_Activity.this,"","加载中...",true,true,null);
        new Thread(runnable).start();
        betaAdapter = new BetaAdapter(Xiaoshuo_zhangjie_List_Activity.this,R.layout.betalistitem,BetaList);
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
                        mainurl = string_page_select_list[page-1];
                        i=0;
                        BetaList.clear();
                        new Thread(runnable).start();
                        progressDialog = ProgressDialog.show(Xiaoshuo_zhangjie_List_Activity.this,"","加载中",true);
                    }
                    else
                    {
                        WriteXiaoshuoJiluFile(((TextView)view.findViewById(R.id.betatext)).getText().toString()+mainurl);
                        Intent intent = new Intent(Xiaoshuo_zhangjie_List_Activity.this,Xiaoshuo_content_Activity.class);
                        intent.putExtra("BTURL",url[a]);
                        intent.putExtra("GENGXINURL",gengxinurl);
                        startActivity(intent);
                    }
                }
                else if((a == listView.getCount()-1)&&(page != string_page_select_list.length))
                {
                    page++;
                    mainurl = string_page_select_list[page-1];
                    i=0;
                    BetaList.clear();
                    new Thread(runnable).start();
                    progressDialog = ProgressDialog.show(Xiaoshuo_zhangjie_List_Activity.this,"","加载中",true);

                }
                else
                {
                    WriteXiaoshuoJiluFile(((TextView)view.findViewById(R.id.betatext)).getText().toString()+mainurl);
                    Intent intent = new Intent(Xiaoshuo_zhangjie_List_Activity.this,Xiaoshuo_content_Activity.class);
                    intent.putExtra("BTURL",url[a]);
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

    void WriteXiaoshuoJiluFile(String xurl)
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
                writer.write(xurl);
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
