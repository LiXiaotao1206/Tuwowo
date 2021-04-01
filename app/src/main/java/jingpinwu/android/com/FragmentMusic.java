package jingpinwu.android.com;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class FragmentMusic extends Fragment
{
    private View view;
    private ListView listView;
    private ProgressDialog progressDialog;
    private BetaAdapter adapter;
    private List<Beta> list;
    private String musicUrl="http://www.mlwei.com/music/so/";
    private List<String> songIdList;

    private int clickItemPosition = 0;
    private List<Mp3Data> mp3DataList;
    private String sendstr;


    private String keyWords = "龚子婕";
    private int page = 1;
    private String source = "kugou";

    private boolean connOverFlg = true;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    //更新MP3列表并告诉service更新了
                    App app = (App)getActivity().getApplication();
                    app.mp3DataList.clear();
                    app.mp3DataList.addAll(mp3DataList);
                    if(source.contains("netease"))
                    {
                        app.wyMp3DataList.clear();
                        app.wyMp3DataList.addAll(mp3DataList);
                    }
                    else if(source.contains("kugou"))
                    {
                        app.kwMp3DataList.clear();
                        app.kwMp3DataList.addAll(mp3DataList);
                    }
                    else if(source.contains("migu"))
                    {
                        app.mgMp3DataList.clear();
                        app.mgMp3DataList.addAll(mp3DataList);
                    }
                    Intent intent = new Intent();
                    intent.setAction("MUSIC_IN");
                    intent.putExtra("music","mp3update");
                    getActivity().sendBroadcast(intent);
                    break;
                case 2:
                    if(!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 3:
                    Toast.makeText(getContext(),"服务器维护中，请稍后重试！",Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    //发送广播，告诉service播放第poistion首歌
                    Intent intentb = new Intent();
                    intentb.setAction("MUSIC_IN");
                    intentb.putExtra("music","playmusicindex");
                    intentb.putExtra("mp3index",clickItemPosition);
                    getActivity().sendBroadcast(intentb);
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
            if(!connOverFlg)
            {
                return;
            }
            connOverFlg = false;
            Message messageShow = new Message();
            messageShow.what = 2;
            handler.sendMessage(messageShow);
            try
            {
                HttpURLConnection connection = (HttpURLConnection) new URL(musicUrl).openConnection();
                // 设置请求方式
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                //设置header
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                // 设置容许输出输入
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //填充请求数据
                OutputStream os = connection.getOutputStream();
                sendstr = "input="+URLEncoder.encode(keyWords, "utf-8")+"&filter=name&type="+source+"&page="+page;
                os.write(sendstr.getBytes());
                os.flush();

                if(connection.getResponseCode()==200)
                {
                    String jsonstr = "";
                    byte[] bytes = new byte[1024];
                    InputStream is = connection.getInputStream();
                    int len ;
                    while((len = is.read(bytes))!=-1)
                    {
                        jsonstr += new String(bytes,0,len);
                    }
                    is.close();
                    JSONObject jsonObject = new JSONObject(jsonstr);
                    String resCode = jsonObject.getString("code");
                    if(resCode!=null)
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        list.clear();
                        songIdList.clear();
                        mp3DataList.clear();
                        if(page!=1)
                        {
                            list.add(new Beta("上页","第"+(page-1)+"页",""));
                            mp3DataList.add(new Mp3Data("","","","",""));
                        }
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            if(jsonArray.getJSONObject(i).getString("url")!=null)
                            {
                                String mp3url = jsonArray.getJSONObject(i).getString("url");
                                String lrcurl = jsonArray.getJSONObject(i).getString("lrc");
                                String title = jsonArray.getJSONObject(i).getString("title");
                                String author = jsonArray.getJSONObject(i).getString("author");
                                String picurl = jsonArray.getJSONObject(i).getString("pic");
                                list.add(new Beta(title, author, picurl));
                                Mp3Data mp3Data = new Mp3Data(mp3url,lrcurl,title,author,picurl);
                                mp3DataList.add(mp3Data);
                            }
                        }
                        {
                            list.add(new Beta("下页", "第" + (page + 1) + "页", ""));
                            mp3DataList.add(new Mp3Data("","","","",""));
                        }
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                }
                else
                {
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                }

            }
            catch(IOException e)
            {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
            catch(JSONException e)
            {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
            connOverFlg = true;
        }
    };
    //只在刚打开时，网易页使用，用于获取热度排行榜
    private Runnable runnable_start = new Runnable()
    {
        @Override
        public void run()
        {
            Message messageShow = new Message();
            messageShow.what = 2;
            handler.sendMessage(messageShow);
            try
            {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mlwei.com/music/api/wy/?key=523077333&type=songlist&id=3778678").openConnection();
                // 设置请求方式
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                //设置header
                connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                connection.setRequestProperty("upgrade-insecure-requests", "1");
                // 设置容许输出输入
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //填充请求数据
                OutputStream os = connection.getOutputStream();
                String args = "key=523077333&cache=1&type=songlist&id=3778678";
                os.write(args.getBytes());
                os.flush();

                if(connection.getResponseCode()==200)
                {
                    String jsonstr = "";
                    byte[] bytes = new byte[1024];
                    InputStream is = connection.getInputStream();
                    int len ;
                    while((len = is.read(bytes))!=-1)
                    {
                        jsonstr += new String(bytes,0,len);
                    }
                    is.close();
                    JSONObject jsonObject = new JSONObject(jsonstr);
                    String resCode = jsonObject.getString("Code");
                    if(resCode!=null && resCode.equals("OK"))
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("Body");
                        list.clear();
                        songIdList.clear();
                        mp3DataList.clear();

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            if(jsonArray.getJSONObject(i).getString("url")!=null)
                            {
                                String mp3url = jsonArray.getJSONObject(i).getString("url");
                                String lrcurl = jsonArray.getJSONObject(i).getString("lrc");
                                String title = jsonArray.getJSONObject(i).getString("title");
                                String author = jsonArray.getJSONObject(i).getString("author");
                                String picurl = jsonArray.getJSONObject(i).getString("pic");
                                list.add(new Beta(title, author, picurl));
                                Mp3Data mp3Data = new Mp3Data(mp3url,lrcurl,title,author,picurl);
                                mp3DataList.add(mp3Data);
                            }
                        }

                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                }
                else
                {
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                }

            }
            catch(IOException e)
            {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
            catch(JSONException e)
            {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
        }
    };
    public FragmentMusic()
    {
        super();
        songIdList = new ArrayList<>();

    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public void search(String keywords)
    {
        this.keyWords = keywords;
        page = 1;
        new Thread(runnable).start();
    }

    public List<Mp3Data> getMusicList()
    {
        App app = (App)getActivity().getApplication();
        mp3DataList.clear();
        if(source.contains("kugou"))
        {
            mp3DataList.addAll(app.kwMp3DataList);
            return app.kwMp3DataList;
        }
        else if(source.contains("migu"))
        {
            mp3DataList.addAll(app.mgMp3DataList);
            return app.mgMp3DataList;
        }
        else
        {
            mp3DataList.addAll(app.wyMp3DataList);
            return app.wyMp3DataList;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
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

        mp3DataList = new ArrayList<>();
        view = getLayoutInflater().inflate(R.layout.layout_betalist,null);
        listView = view.findViewById(R.id.betalistview);
        list = new ArrayList<>();
        adapter = new BetaAdapter(getContext(),R.layout.betalistitem,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(page>1 && position==0)
                {
                    page--;
                    new Thread(runnable).start();
                }
                else if(position==list.size()-1)
                {
                    if(!mp3DataList.isEmpty())
                    {
                        page++;
                        new Thread(runnable).start();
                    }
                }
                else
                {
                    clickItemPosition = position;
                    if(mp3DataList.size()<=clickItemPosition)
                    {
                        Toast.makeText(getActivity(),"音乐列表为空，请重新选择音乐！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getContext(),"正在准备播放  "+mp3DataList.get(clickItemPosition).title+" - "+mp3DataList.get(clickItemPosition).author,Toast.LENGTH_LONG).show();
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            //播放
                            Message message = new Message();
                            message.what = 4;
                            handler.sendMessage(message);
                        }
                    }).start();

                }
            }
        });

        //只让第一个页面（网易云）在启动时加载
        if(source.equals("netease"))
        {
            new Thread(runnable_start).start();
        }
        else
        {
            list.add(new Beta("请输入音乐名称或歌手搜索","兔喔喔音乐整合平台",""));
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public class Mp3Data
    {
        public String mp3url;
        public String lrcurl;
        public String title;
        public String author;
        public String picurl;

        public Mp3Data(String mp3url,String lrcurl,String title,String author,String picurl)
        {
            this.mp3url = mp3url;
            this.lrcurl = lrcurl;
            this.title = title;
            this.author = author;
            this.picurl = picurl;
        }
    }
}
