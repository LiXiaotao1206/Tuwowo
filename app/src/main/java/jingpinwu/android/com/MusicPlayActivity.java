package jingpinwu.android.com;

import android.app.DownloadManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.graphics.Color;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;

import android.widget.ImageView;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.wcy.lrcview.LrcView;

public class MusicPlayActivity extends AppCompatActivity
{
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    private LrcView lrcView;
    private SeekBar seekBar;
    private ImageView btnPlayPause;
    private ImageView btnPlayPrevious;
    private ImageView btnPlayNext;
    private ImageView btnDownload;
    private MyImageView imgSongPic;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvCurTime;
    private TextView tvAllTime;
    private jingpinwu.android.com.BitmapLinearLayout bkLayout;
    private String mp3url="";
    private String lrc="";
    private String curTimeStr="00:00";
    private String allTimeStr="00:00";
    private String title;
    private String author;
    private String lrcText = "";//重新根据url解析的lrc
    private Thread lrcThread;
    private MusicPlayActivity.MyBroadcastReceiver broadcastReceiver;

    private MusicPlayService musicPlayService = null;
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder)
        {
            MusicPlayService.MusicBinder binder = (MusicPlayService.MusicBinder)ibinder;
            musicPlayService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicPlayService = null;
        }
    };
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==100)
            {
                btnPlayPause.setImageDrawable(getDrawable(R.drawable.play_btn));
            }
            else if(msg.what==200)
            {
                lrcView.updateTime(musicPlayService.getMusicCurPlayTime());
                seekBar.setProgress((int) musicPlayService.getMusicCurPlayTime());
                tvAllTime.setText(allTimeStr);
                tvCurTime.setText(curTimeStr);
                btnPlayPause.setImageDrawable(getDrawable(R.drawable.pause_btn));
            }
            else if(msg.what==1000)
            {
                lrcView.loadLrc(lrcText);
            }
            super.handleMessage(msg);
        }
    };
    private Runnable runnable_refresh_play = new Runnable()
    {
        @Override
        public void run()
        {
            if(musicPlayService.mediaPlayerIsPlaying())
            {
                long timeall = musicPlayService.getMusicAllTime();
                long buf = timeall/1000/60;
                if(buf<10)
                {
                    allTimeStr = "0" + buf + ":";
                }
                else
                {
                    allTimeStr = "" + buf + ":";
                }
                buf = timeall/1000%60;
                if(buf<10)
                {
                    allTimeStr += "0" + buf;
                }
                else
                {
                    allTimeStr += "" + buf;
                }
                long curPlayTime = musicPlayService.getMusicCurPlayTime();
                buf = curPlayTime/1000/60;
                if(buf<10)
                {
                    curTimeStr = "0" + buf + ":";
                }
                else
                {
                    curTimeStr = "" + buf + ":";
                }
                buf = curPlayTime/1000%60;
                if(buf<10)
                {
                    curTimeStr += "0" + buf;
                }
                else
                {
                    curTimeStr += "" + buf;
                }
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
            else
            {
                Message message = new Message();
                message.what = 100;
                handler.sendMessage(message);
            }
            handler.postDelayed(this, 300);
        }
    };
    private Runnable runnable_lrc = new Runnable()
    {
        @Override
        public void run()
        {
            lrcText="";
            try
            {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(lrc).openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(1000000);
                connection.setRequestProperty("Upgrade-Insecure-Requests","1");
                OutputStream os = connection.getOutputStream();
                os.write(lrc.substring(lrc.indexOf("key=")).getBytes());
                os.flush();
                if(connection.getResponseCode()==200)
                {
                    InputStream is = connection.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    while((len=is.read(bytes))!=-1)
                    {
                        lrcText += new String(bytes,0,len);
                    }
                    is.close();
                    Message message = new Message();
                    message.what = 1000;
                    handler.sendMessage(message);
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume()
    {
        wakeLock.acquire();
        super.onResume();
        Intent intent = new Intent();
        intent.setAction("MUSIC_IN");
        intent.putExtra("music","resume");
        sendBroadcast(intent);
    }

    @Override
    protected void onPause()
    {
        wakeLock.release();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_music);
        bkLayout = (jingpinwu.android.com.BitmapLinearLayout)findViewById(R.id.music_play_bk_linearlayout);
        //屏幕常亮
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "myapp:lock");

        lrcThread = new Thread(runnable_lrc);

        broadcastReceiver = new MusicPlayActivity.MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction("MUSIC_OUT");
        // 3. 动态注册：调用Context的registerReceiver（）方法
        registerReceiver(broadcastReceiver,intentFilter);

        //service
        Intent intent = new Intent(this, MusicPlayService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        tvCurTime = (TextView)findViewById(R.id.music_btm_tv_time_current);
        tvAllTime = (TextView)findViewById(R.id.music_btm_tv_time_all);
        imgSongPic = (MyImageView) findViewById(R.id.music_btm_imgv);
        tvTitle = (TextView)findViewById(R.id.tv_music_title);
        tvAuthor = (TextView)findViewById(R.id.tv_music_author);
        lrcView = findViewById(R.id.lrc_view);
        seekBar = findViewById(R.id.music_btm_seekbar);
        btnPlayPause = (ImageView)findViewById(R.id.music_btm_play);
        btnPlayPrevious = (ImageView)findViewById(R.id.music_btm_previous);
        btnPlayNext = (ImageView)findViewById(R.id.music_btm_next);
        btnDownload = (ImageView)findViewById(R.id.music_btm_download);

        lrcView.setDraggable(true, new LrcView.OnPlayClickListener()
        {
            @Override
            public boolean onPlayClick(long time)
            {
                musicPlayService.mediaPlayerSetProgress((int) time);
                if(!musicPlayService.mediaPlayerIsPlaying())
                {
                    musicPlayService.mediaPlayerPlay();
                    handler.post(runnable_refresh_play);
                }
                return true;
            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!musicPlayService.mediaPlayerIsPlaying())
                {
                    if(musicPlayService.getMusicAllTime()==0)
                    {
                        Toast.makeText(getApplicationContext(),"正在缓冲...",Toast.LENGTH_SHORT).show();
                    }
                    musicPlayService.mediaPlayerPlay();
                    btnPlayPause.setImageDrawable(getDrawable(R.drawable.pause_btn));
                    handler.post(runnable_refresh_play);
                }
                else
                {
                    musicPlayService.mediaPlayerPause();
                    btnPlayPause.setImageDrawable(getDrawable(R.drawable.play_btn));
                    handler.removeCallbacks(runnable_refresh_play);
                }
            }
        });

        btnPlayPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"正在切换到上一首",Toast.LENGTH_LONG).show();
                musicPlayService.mediaPlayerPrevious();
            }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"正在切换到下一首",Toast.LENGTH_LONG).show();
                musicPlayService.mediaPlayerNext();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Vibrator vibrator = (Vibrator)MusicPlayActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(600);
                Uri uritishi = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone rt = RingtoneManager.getRingtone(MusicPlayActivity.this, uritishi);
                rt.play();
                Toast.makeText(MusicPlayActivity.this,title+"正在下载...", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                Uri uri=Uri.parse(mp3url);
                DownloadManager.Request request=new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
                request.setVisibleInDownloadsUi(true);
                request.setDescription("正在下载至   兔喔喔   文件夹");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir("兔喔喔",title+" - "+author+".mp3");
                downloadManager.enqueue(request);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                musicPlayService.mediaPlayerSetProgress(seekBar.getProgress());
                lrcView.updateTime(seekBar.getProgress());
            }
        });

    }

    void resetUI()
    {
        if(musicPlayService==null)
        {
            Log.e("AAAA","music service = null");
            return;
        }
        App app = (App)getApplication();
        int index_cur = musicPlayService.getMusicCurPlayIndex();
        if(index_cur<0 || index_cur>=app.mp3DataList.size())
        {
            Toast.makeText(MusicPlayActivity.this,"请重新选择音乐播放！",Toast.LENGTH_LONG).show();
            MusicPlayActivity.this.finish();
            return;
        }
        FragmentMusic.Mp3Data mp3Data = app.mp3DataList.get(index_cur);

        mp3url = mp3Data.mp3url;
        lrc = mp3Data.lrcurl;
        title = mp3Data.title;
        author = mp3Data.author;

        tvTitle.setText(title);
        tvAuthor.setText(author);
        imgSongPic.setImageURL(mp3Data.picurl);
        seekBar.setMax((int) musicPlayService.getMusicAllTime());
        seekBar.setProgress((int) musicPlayService.getMusicCurPlayTime());
        tvAllTime.setText(allTimeStr);
        tvCurTime.setText(curTimeStr);
        btnPlayPause.setImageDrawable(getDrawable(R.drawable.pause_btn));
        bkLayout.setBackgroundBitmap(mp3Data.picurl);

        if(lrc != null && lrc.length() > 5 && lrc.substring(0, 5).contains("http"))
        {
            lrcThread.interrupt();
            lrcThread = new Thread(runnable_lrc);
            lrcThread.start();
        }
        else
        {
            lrcView.loadLrc(lrc);
        }

        lrcView.updateTime(musicPlayService.getMusicCurPlayTime());
        //启动lrcview更新线程
        handler.post(runnable_refresh_play);
    }

    private String getLrcText(String fileName)
    {
        String lrcText = null;
        try
        {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return lrcText;
    }

    @Override
    protected void onDestroy()
    {
        handler.removeCallbacks(runnable_refresh_play);
        unregisterReceiver(broadcastReceiver);
        unbindService(serviceConnection);

        super.onDestroy();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getStringExtra("music").equals("curindexchange"))
            {
                resetUI();
            }
        }
    }

}

