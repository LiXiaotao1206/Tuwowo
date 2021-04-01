package jingpinwu.android.com;


import android.app.Application;

import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

public class App extends Application
{
    //当前音乐列表
    public List<FragmentMusic.Mp3Data> mp3DataList = new ArrayList<>();
    //网易当前音乐列表
    public List<FragmentMusic.Mp3Data> wyMp3DataList = new ArrayList<>();
    //咪咕当前音乐列表
    public List<FragmentMusic.Mp3Data> mgMp3DataList = new ArrayList<>();
    //酷狗当前音乐列表
    public List<FragmentMusic.Mp3Data> kwMp3DataList = new ArrayList<>();
    boolean x5InitFlg = true;
    int test=0;
    @Override
    public void onCreate()
    {
        super.onCreate();
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished()
            {

            }

            @Override
            public void onViewInitFinished(boolean b)
            {
                Log.e("AAAA", " X5 内核初始化： " + b);
                x5InitFlg = b;
            }
        });
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
