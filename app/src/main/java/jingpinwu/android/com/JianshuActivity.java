package jingpinwu.android.com;


import android.app.AlertDialog;
;
import android.app.ProgressDialog;
;
import android.content.DialogInterface;
import android.content.Intent;
;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class JianshuActivity extends AppCompatActivity
{

    private BetaAdapter betaAdapter;
    private ListView listView;
    private List<Beta> betaList = new ArrayList<Beta>();
    private List<String> authorList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private int count = 1;

    private String gengxinurl = "";
    private ProgressDialog progressDialog;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 100:
                    betaAdapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(0);
                    break;
                case 200:
                    if(!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    break;
                case 400:
                    Toast.makeText(JianshuActivity.this,"服务器连接失败！",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            String jsonStr = "";
            Message message2 = new Message();
            message2.what = 200;
            handler.sendMessage(message2);
            try
            {
                Document doc = Jsoup.connect("https://www.jianshu.com/asimov/trending/now?count="+count)
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .userAgent("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Mobile Safari/537.36")
                        .get();

                if(doc!=null)
                {
                    jsonStr = doc.select("body").text();
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    betaList.clear();
                    authorList.clear();
                    titleList.clear();
                    urlList.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject joson = new JSONObject(jsonArray.get(i).toString());
                        JSONObject jos = new JSONObject(joson.getJSONObject("object").toString());
                        JSONObject jo = new JSONObject(jos.getJSONObject("data").toString());
                        JSONObject joAuthor = new JSONObject(jo.getJSONObject("user").toString());

                        titleList.add(jo.getString("title"));
                        authorList.add(joAuthor.getString("nickname"));
                        urlList.add("https://www.jianshu.com/p/"+jo.getString("slug"));
                        String imgurl = jo.getString("list_image_url");
                        String authorimgurl = joAuthor.getString("avatar");

                        if(imgurl!=null && imgurl.length()>4)
                        {
                            betaList.add(new Beta(jo.getString("title"), "作者：" + joAuthor.getString("nickname"),imgurl ));
                        }
                        else if(authorimgurl!=null)
                        {
                            betaList.add(new Beta(jo.getString("title"), "作者：" + joAuthor.getString("nickname"),authorimgurl ));
                        }
                    }
                    {
                        titleList.add("");
                        authorList.add("");
                        urlList.add("https://www.jianshu.com/p/298c12968c49");
                        betaList.add(new Beta("刷新", "更多精彩内容...","" ));
                    }
                    Message message = new Message();
                    message.what = 100;
                    handler.sendMessage(message);
                }
                else
                {
                    Message message = new Message();
                    message.what = 400;
                    handler.sendMessage(message);
                }
            }
            catch(IOException e)
            {
                Log.e("AAAA",e.getMessage());
                Message message = new Message();
                message.what = 400;
                handler.sendMessage(message);
            }
            catch(JSONException e)
            {Log.e("AAAA",e.getMessage());
                Message message = new Message();
                message.what = 400;
                handler.sendMessage(message);
            }

            Message message = new Message();
            message.what = 300;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_betalist);
        gengxinurl = getIntent().getExtras().getString("GENGXINURL");
        progressDialog = new ProgressDialog(this);
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
        listView = (ListView)findViewById(R.id.betalistview);
        betaAdapter = new BetaAdapter(this,R.layout.betalistitem,betaList);
        listView.setAdapter(betaAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(i<urlList.size()-1)
                {
                    Intent intent = new Intent(JianshuActivity.this, JSContentActivity.class);
                    intent.putExtra("JSURL", urlList.get(i));
                    intent.putExtra("GENGXINURL",gengxinurl);
                    startActivity(intent);
                }
                else
                {
                    count++;
                    new Thread(runnable).start();
                }
            }
        });

        new Thread(runnable).start();
    }
}
