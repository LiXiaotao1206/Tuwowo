package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSheetActivity extends Activity
{
    Document document;
    Elements elements;
    ListView listView;
    Button button;
    EditText editText;
    String searchContent = "LPC1768";
    SimpleAdapter adapter;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    String[] url = new String[100];
    String[] name = new String[100];
    String url2="http://datasheet.eeworld.com.cn/part/IRD-LPC1768-DEV,Future%20Designs%20Inc,7962931.html";
    String pdfurl="";
    int itemId = -1;
    int pages=1;
    int currentpage=1;
    boolean flg_over = true;
    ProgressDialog progressDialog;
    float currentbytes = 0;
    float allbytes = 1;
    long downloadId = 1;
    float jindu = 0;
    int clickposition = 1;
    ProgressDialog dialog;
    DownloadManager downloadManager;
    DownloadManager.Query query;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what==1)
            {
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
            if(msg.what==2)
            {
                Toast.makeText(DataSheetActivity.this,"请检查网络连接！",Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
            if(msg.what==3)
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                //dialog.show();
                //new Thread(runnableQuery).start();
                new AlertDialog.Builder(DataSheetActivity.this).setMessage("手册正在下载到手机存储 兔喔喔 文件夹！").setPositiveButton("ok",null).create().show();
                Download(pdfurl);
            }
            if(msg.what==4)
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                new AlertDialog.Builder(DataSheetActivity.this).setTitle("下载完成：").setMessage("文件手册已下载至 兔喔喔 文件夹！").setPositiveButton("OK",null).create().show();
            }
        }
    };
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            flg_over = false;
            try
            {
                document = Jsoup.connect("http://datasheet.eeworld.com.cn/search.html?from=datasheet&t=partno&k="+searchContent+"&pageno="+currentpage).timeout(30000).get();
                elements = document.select(".list-con").select(".table").select(".mt20").select("tr");
                if(currentpage==1)
                {
                    pages = document.select(".page").select("a").size();
                    if (pages >= 7)
                    {
                        pages = pages - 5;
                    }
                }
                itemId=-1;
                data.clear();
                if(currentpage>1)
                {
                    itemId++;
                    Map<String, String> mapadd = new HashMap<String, String>();
                    mapadd.put("name", "上一页");
                    mapadd.put("changshang", "转向第" + (currentpage - 1) + "页");
                    data.add(mapadd);
                    url[itemId] = "";
                }
                if(document.toString().contains("正在修复中，请稍后"))
                {
                    itemId++;
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "该内容服务器正在修复中...");
                    map.put("changshang", "请稍后重试！");
                    data.add(map);
                    url[itemId] = "正在修复中，请稍后";
                    itemId++;
                }
                else
                {
                    if(elements!=null)
                    {
                        for (Element e : elements)
                        {
                            int a = 0;
                            if (currentpage > 1)
                            {
                                a = 1;
                            }
                            if (itemId >= a)
                            {
                                if(e.select("td").size()>3)
                                {
                                    url[itemId] = "http://datasheet.eeworld.com.cn/"+e.select("td").get(3).select("a").attr("href");
                                    if(url[itemId].contains(".html"))
                                    {
                                        name[itemId] = e.select("td").get(0).select("a").attr("title").toString();
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("name", name[itemId]);
                                        String cs = e.select("td").get(1).select("a").attr("title").toString();
                                        cs = "品牌：" + cs.substring(0, cs.length() - 2);
                                        map.put("changshang", cs);
                                        data.add(map);
                                    }
                                    else
                                    {
                                        itemId--;
                                    }
                                }
                                else
                                {
                                    data.clear();
                                    Map<String,String> map = new HashMap<String, String>();
                                    map.put("name","无搜索结果");
                                    map.put("changshang","请重新输入关键字搜索");
                                    data.add(map);
                                    url[0] = "正在修复中，请稍后";
                                }
                            }
                            itemId++;
                        }
                    }
                    else
                    {
                        data.clear();
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("name","无搜索结果");
                        map.put("changshang","请重新输入关键字搜索");
                        data.add(map);
                        url[0] = "正在修复中，请稍后";
                    }
                }
                if(currentpage<pages)
                {
                    Map<String, String> mapadd = new HashMap<String, String>();
                    mapadd.put("name", "下一页");
                    mapadd.put("changshang", "转向第" + (currentpage + 1) + "页");
                    data.add(mapadd);
                    url[itemId] = "";
                    itemId++;
                }
                if(itemId<0)
                {
                    data.clear();
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("name","无搜索结果");
                    map.put("changshang","请重新输入关键字搜索");
                    data.add(map);
                    url[0] = "正在修复中，请稍后";
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
            catch (IOException e)
            {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
            flg_over = true;
        }
    };
    Runnable runnable2 = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                Document document = Jsoup.connect(url2).timeout(30000).get();
                String s = document.toString();
                s= s.substring(s.indexOf("<a href=\"/pdf")+9,s.length());
                pdfurl = "http://datasheet.eeworld.com.cn"+s.substring(0,s.indexOf(".pdf")+4);
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    };
    Runnable runnableQuery = new Runnable()
    {
        @Override
        public void run()
        {
            while (jindu<100)
            {
                long start = System.currentTimeMillis();
                query();
                jindu = 100*currentbytes/allbytes;
                if(dialog.isShowing())
                {
                    dialog.setProgress((int) jindu);
                }
                long stop = System.currentTimeMillis();
                try
                {
                    if (stop - start < 300)
                    {
                        Thread.sleep(300 - (stop - start));
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            //if(jindu>=100)
            //{
                //Message message = new Message();
                //message.what = 4;
                //handler.sendMessage(message);
           // }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_datasheet);
        button = (Button)findViewById(R.id.btn_datasheet);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        query = new DownloadManager.Query();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        //dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
        //dialog.setTitle("提示");
        dialog.setMax(100);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "后台下载",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //downloadManager.remove(downloadId);
                        dialog.dismiss();
                    }
                });
        dialog.setMessage("正在加载，请稍候...");

        editText = (EditText)findViewById(R.id.edittext_datasheet);
        adapter = new SimpleAdapter(this,data,R.layout.listitem,new String[]{"name","changshang"},new int[]{R.id.item_title,R.id.item_text});
        listView = (ListView)findViewById(R.id.listview_datasheet);
        listView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(flg_over==true)
                {
                    if (editText.getText().toString().length() < 1)
                    {
                        Toast.makeText(DataSheetActivity.this, "请输入芯片名称或型号！", Toast.LENGTH_LONG).show();
                        searchContent = "LPC1768";
                    }
                    else
                    {
                        pages=1;
                        currentpage=1;
                        editText.clearFocus();
                        hideInput();
                        searchContent = editText.getText().toString();
                        progressDialog = ProgressDialog.show(DataSheetActivity.this, "", "请稍等...", true, true, null);
                        new Thread(runnable).start();
                    }
                }
                else
                {
                    Toast.makeText(DataSheetActivity.this, "服务器繁忙，请稍后重试！", Toast.LENGTH_LONG).show();
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(itemId>0)
                {
                    clickposition = position;
                    url2=url[position];
                    if(url2.length()<1)
                    {
                        if(position==0)
                        {
                            currentpage--;
                        }
                        else
                        {
                            currentpage++;
                        }
                        progressDialog = ProgressDialog.show(DataSheetActivity.this, "", "请稍等...", true, true, null);
                        new Thread(runnable).start();
                    }
                    else
                    {
                        if(url2.contains("修复"))
                        {
                            Toast.makeText(DataSheetActivity.this,"该内容正在修复，请稍后重试！",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            progressDialog = ProgressDialog.show(DataSheetActivity.this, "", "正在查询资源服务器，请稍等...", true, true, null);
                            new Thread(runnable2).start();
                        }
                    }
                }
                else
                {
                    Toast.makeText(DataSheetActivity.this,"请重新搜索！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v)
        {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /*DownloadManager是android2.3以后，系统下载的方法，是处理长期运行的HTTP下载的系统服务。
    客户端可以请求的URI被下载到一个特定的目标文件。
    客户端将会在后台与http交互进行下载，或者在下载失败，或者连接改变，重新启动系统后重新下载。
    还可以进入系统的下载管理界面查看进度。DownloadManger有两个内部类，Request 和Query。
    Request类可设置下载的一些属性。Query类可查询当前下载的进度，下载地址，文件存放目录等数据。
     */
    void Download(String durl)
    {
        Vibrator vibrator = (Vibrator) DataSheetActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(600);
        Uri uri=Uri.parse(durl);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
        request.setVisibleInDownloadsUi(true);
        request.setDescription("芯片手册正在下载至   兔喔喔   文件夹");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("兔喔喔",name[clickposition]+".pdf");
        downloadId = downloadManager.enqueue(request);//每下载的一个文件对应一个id，通过此id可以查询数据。
    }
    void query()
    {
        Cursor cursor = downloadManager.query(query.setFilterById(downloadId));
        if (cursor != null && cursor.moveToFirst())
        {
            //下载的文件到本地的目录
            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            //已经下载的字节数
            currentbytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            //总需下载的字节数
            allbytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            //Notification 标题
            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            //描述
            String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            //下载对应id
            long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            //下载文件的URL链接
            String downloadurl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
        }
        cursor.close();
    }

}