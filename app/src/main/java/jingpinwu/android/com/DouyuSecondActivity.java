package jingpinwu.android.com;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class DouyuSecondActivity extends AppCompatActivity
{
    private BetaAdapter betaAdapter;
    private ListView listView;
    private List<Beta> betaList = new ArrayList<Beta>();


    private List<Integer> cate2IdList = new ArrayList<>();
    private List<String> iconList = new ArrayList<>();
    private List<String> cate2NameList = new ArrayList<>();
    private List<String> shortNameList = new ArrayList<>();

    private int cate1Id = 1;

    private ProgressDialog progressDialog;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 100:
                    betaAdapter.notifyDataSetChanged();
                    break;
                case 200:
                    if(!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 300:
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
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
                Document doc = Jsoup.connect("https://m.douyu.com/api/cate/list")
                        .maxBodySize(0)
                        .userAgent("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Mobile Safari/537.36")
                        .timeout(5000)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .header("upgrade-insecure-requests","1")
                        .header("cache-control","max-age=0")
                        .get();
                if(doc!=null)
                {
                    jsonStr = doc.select("body").text();
                    JSONObject jsonObjectA = new JSONObject(jsonStr);
                    JSONObject jsonObject = jsonObjectA.getJSONObject("data");
                    JSONArray jsonArray = jsonObject.getJSONArray("cate2Info");
                    betaList.clear();
                    cate2IdList.clear();
                    cate2NameList.clear();
                    iconList.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jo = new JSONObject(jsonArray.get(i).toString());
                        if(jo.getInt("cate1Id")==cate1Id)
                        {
                            cate2IdList.add(jo.getInt("cate2Id"));
                            cate2NameList.add(jo.getString("cate2Name"));
                            iconList.add(jo.getString("icon"));
                            shortNameList.add(jo.getString("shortName"));

                            betaList.add(new Beta(jo.getString("cate2Name"),"来源：兔喔喔第"+jo.getInt("cate2Id")+"网络直播频道",jo.getString("icon")));
                        }
                    }
                    Message message = new Message();
                    message.what = 100;
                    handler.sendMessage(message);
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
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
        betaAdapter = new BetaAdapter(DouyuSecondActivity.this,R.layout.betalistitem,betaList);
        listView.setAdapter(betaAdapter);

        cate1Id = getIntent().getIntExtra("cate1Id",1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(DouyuSecondActivity.this,DouyuThirdActivity.class);
                intent.putExtra("shortName",shortNameList.get(i));
                startActivity(intent);
            }
        });

        new Thread(runnable).start();
    }
}
