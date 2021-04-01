package jingpinwu.android.com;

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
import android.widget.ListView;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DouyuThirdActivity extends AppCompatActivity
{
    private BetaAdapter betaAdapter;
    private ListView listView;
    private List<Beta> betaList = new ArrayList<Beta>();


    private List<Integer> ridList = new ArrayList<>();
    private List<String> iconList = new ArrayList<>();
    private List<String> roomNameList = new ArrayList<>();
    private List<String> zhuboNameList = new ArrayList<>();
    private List<String> renqiList = new ArrayList<>();

    private String shortName = "DOTA2";
    private int page = 1;

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
                    listView.setSelection(0);
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
            Message message2 = new Message();
            message2.what = 200;
            handler.sendMessage(message2);
            try
            {

                Document doc = Jsoup.connect("https://m.douyu.com/api/room/list?page="+page+"&type="+shortName)
                        .maxBodySize(0)
                        .userAgent("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Mobile Safari/537.36")
                        .timeout(5000)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .header("upgrade-insecure-requests","1")
                        .get();
                if(doc!=null)
                {
                    JSONObject jsonObjectA = new JSONObject(doc.select("body").text());
                    JSONObject jsonObject = jsonObjectA.getJSONObject("data");
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    betaList.clear();
                    ridList.clear();
                    roomNameList.clear();
                    iconList.clear();
                    zhuboNameList.clear();

                    if(page!=1)
                    {
                        ridList.add(1);
                        roomNameList.add("");
                        iconList.add("");
                        zhuboNameList.add("");
                        renqiList.add("");
                        betaList.add(new Beta("上一页","第"+(page-1)+"页",""));
                    }
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jo = new JSONObject(jsonArray.get(i).toString());
                        ridList.add(jo.getInt("rid"));
                        roomNameList.add(jo.getString("roomName"));
                        iconList.add(jo.getString("roomSrc"));
                        zhuboNameList.add(jo.getString("nickname"));
                        renqiList.add(jo.getString("hn"));
                        betaList.add(new Beta(jo.getString("roomName"),"主播："+jo.getString("nickname")+" -  [ "+jo.getString("hn")+"人在看 ]",jo.getString("roomSrc")));
                    }
                    {
                        ridList.add(1);
                        roomNameList.add("");
                        iconList.add("");
                        zhuboNameList.add("");
                        renqiList.add("");
                        betaList.add(new Beta("下一页", "第" + (page + 1) + "页", ""));
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
        betaAdapter = new BetaAdapter(DouyuThirdActivity.this,R.layout.douyu_third_item,betaList);
        listView.setAdapter(betaAdapter);

        shortName = getIntent().getStringExtra("shortName");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(page!=1 && i==0)
                {
                    page--;
                    new Thread(runnable).start();
                }
                else if(i==roomNameList.size()-1)
                {
                    page++;
                    new Thread(runnable).start();
                }
                else
                {
                    Intent intent = new Intent(DouyuThirdActivity.this,XLVideoActivity.class);
                    intent.putExtra("XLVIDEOURL","https://m.douyu.com/"+ridList.get(i)+"?type="+shortName);
                    intent.putExtra("XLVIDEOFLG","");
                    startActivity(intent);
                }
            }
        });

        new Thread(runnable).start();
    }

}
