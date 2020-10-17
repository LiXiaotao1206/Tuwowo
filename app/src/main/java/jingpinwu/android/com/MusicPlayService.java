package jingpinwu.android.com;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicPlayService extends Service
{
    private Notification notificationMusic;
    private NotificationManager notificationManager;
    private RemoteViews remoteViews;

    private MyBroadcastReceiver receiver;
    private boolean isMusicPlayActivityShowing = true;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private List<FragmentMusic.Mp3Data> mp3DataList = new ArrayList<>();
    private int curPlayIndex = 0;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    Log.e("AAAA","previous");
                    curPlayIndex--;
                    if(mp3DataList.size()>curPlayIndex && curPlayIndex>=0)
                    {
                        mediaPlayerSetData(mp3DataList.get(curPlayIndex).mp3url);
                        remoteViews.setTextViewText(R.id.music_notification_title, mp3DataList.get(curPlayIndex).title+" - "+mp3DataList.get(curPlayIndex).author);
                        notificationManager.notify(1,notificationMusic);
                    }
                    else if(curPlayIndex<0)
                    {
                        curPlayIndex = -1;
                    }
                    break;
                case 2:
                    if(mediaPlayer.isPlaying())
                    {
                        Log.e("AAAA","pause");
                        remoteViews.setImageViewResource(R.id.music_notification_play, R.drawable.play_notify);
                        notificationManager.notify(1,notificationMusic);
                        mediaPlayerPause();
                    }
                    else
                    {
                        Log.e("AAAA","paly");
                        remoteViews.setImageViewResource(R.id.music_notification_play, R.drawable.pause_notify);
                        notificationManager.notify(1,notificationMusic);
                        mediaPlayerPlay();
                    }
                    break;
                case 3:
                    Log.e("AAAA","next");
                    curPlayIndex++;
                    if(mp3DataList.size()>curPlayIndex && curPlayIndex>=0)
                    {
                        mediaPlayerSetData(mp3DataList.get(curPlayIndex).mp3url);
                        remoteViews.setTextViewText(R.id.music_notification_title, mp3DataList.get(curPlayIndex).title+" - "+mp3DataList.get(curPlayIndex).author);
                        notificationManager.notify(1,notificationMusic);
                    }
                    break;
                case 4:
                    Log.e("AAAA","exit");
                    if(!isMusicPlayActivityShowing)
                    {
                        MusicPlayService.this.stopSelf();
                    }
                    break;
                case 5:
                    Log.e("AAAA","stop");
                    isMusicPlayActivityShowing = false;

                    break;
                case 6:
                    Log.e("AAAA","resume");
                    isMusicPlayActivityShowing = true;

                    //告诉外界播放的音乐索引
                    Intent intent = new Intent();
                    intent.setAction("MUSIC_OUT");
                    intent.putExtra("music","curindexchange");
                    intent.putExtra("curplay",curPlayIndex);
                    if(curPlayIndex<mp3DataList.size())
                    {
                        sendBroadcast(intent);
                    }
                    break;
                case 7:
                    Log.e("AAAA","playmusicindex");
                    if(mp3DataList.size()>curPlayIndex)
                    {
                        mediaPlayerSetData(mp3DataList.get(curPlayIndex).mp3url);
                        remoteViews.setTextViewText(R.id.music_notification_title, mp3DataList.get(curPlayIndex).title+" - "+mp3DataList.get(curPlayIndex).author);
                        notificationManager.notify(1,notificationMusic);
                    }
                    break;
                case 8:
                    Log.e("AAAA","mp3Listupdate");
                    curPlayIndex = -1;
                    //mediaPlayer.stop();
                    mp3DataList.clear();
                    App app = (App)getApplication();
                    mp3DataList.addAll(app.mp3DataList);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener()
    {
        @Override
        public void onPrepared(MediaPlayer mp)
        {
            Intent intent = new Intent();
            intent.setAction("MUSIC_OUT");
            intent.putExtra("music","curindexchange");
            intent.putExtra("curplay",curPlayIndex);
            if(curPlayIndex<mp3DataList.size())
            {
                sendBroadcast(intent);
                mediaPlayerPlay();
            }
        }
    };
    private boolean ismeadiaPlayerFirstPrepared = true;
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mp)
        {
            //不设置资源，mediaplayer初始化好后也会调用onCompletion，所以第一次屏蔽掉，避免刚传来列表就播放
            if(ismeadiaPlayerFirstPrepared)
            {
                ismeadiaPlayerFirstPrepared = false;
                return;
            }
            //下一首
            Message message=new Message();
            message.what=3;
            handler.sendMessage(message);
        }
    };
    private MusicBinder musicBinder;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("AAAA","music play service start");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        musicBinder = new MusicBinder();

        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction("MUSIC_IN");
        // 3. 动态注册：调用Context的registerReceiver（）方法
        registerReceiver(receiver,intentFilter);
        //创建Notification
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(),R.layout.music_notification);

        Intent previousIntent = new Intent();
        previousIntent.setAction("MUSIC_IN");
        previousIntent.putExtra("music","previous");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this,1,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent();
        playIntent.setAction("MUSIC_IN");
        playIntent.putExtra("music","play");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,2,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent();
        nextIntent.setAction("MUSIC_IN");
        nextIntent.putExtra("music","next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,3,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitIntent = new Intent();
        exitIntent.setAction("MUSIC_IN");
        exitIntent.putExtra("music","exit");
        PendingIntent exitPendingIntent = PendingIntent.getBroadcast(this,4,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.music_notification_previous,previousPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_play,playPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_next,nextPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_close,exitPendingIntent);

        remoteViews.setViewVisibility(R.id.music_notification_close, View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "1"; //根据业务执行
            String channelName = "兔喔喔音乐播放"; //这个是channelid 的解释，在安装的时候会展示给用户看
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
            channel.setSound(null,null);
            channel.setImportance(NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            notificationMusic = new Notification.Builder(this,"1")
                    .setContentTitle("兔喔喔音乐")
                    .setContentText("")
                    .setCustomContentView(remoteViews)
                    .setSmallIcon(R.drawable.notificationmusic)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    //.setContentIntent(pendingIntent)
                    //.setFullScreenIntent(pendingIntent,false)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
        }
        else
        {
            notificationMusic = new Notification.Builder(this)
                    .setContentTitle("兔喔喔音乐")
                    .setContentText("")
                    .setContent(remoteViews)
                    .setSmallIcon(R.drawable.notificationmusic)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    //.setContentIntent(pendingIntent)
                    //.setFullScreenIntent(pendingIntent, false)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setSound(null)
                    .build();
        }
        notificationManager.notify(1,notificationMusic);


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
        notificationManager.cancel(1);
        mediaPlayer.release();
        Log.e("AAAA","music service exit");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return musicBinder;
    }

    long getMusicAllTime()
    {
        return mediaPlayer.getDuration();
    }

    long getMusicCurPlayTime()
    {
        return mediaPlayer.getCurrentPosition();
    }

    int getMusicCurPlayIndex()
    {
        return this.curPlayIndex;
    }

    public void mediaPlayerPlay()
    {
        mediaPlayer.start();
    }

    public void mediaPlayerPause()
    {
        mediaPlayer.pause();
    }

    public void mediaPlayerPrevious()
    {
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
    }

    public void mediaPlayerNext()
    {
        Message message=new Message();
        message.what=3;
        handler.sendMessage(message);
    }

    public boolean mediaPlayerIsPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public void mediaPlayerSetData(String mp3url)
    {
        mediaPlayer.reset();
        try
        {
            mediaPlayer.setDataSource(mp3url);
            mediaPlayer.prepareAsync();
        }
        catch(IOException e)
        {
            //e.printStackTrace();
        }
    }

    public void mediaPlayerSetProgress(int progress)
    {
        mediaPlayer.seekTo(progress);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getStringExtra("music").equals("previous"))
            {
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("music").equals("play"))
            {
                Message message=new Message();
                message.what=2;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("music").equals("next"))
            {
                Message message=new Message();
                message.what=3;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("music").equals("exit"))
            {
                Message message=new Message();
                message.what=4;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("music").equals("stop"))
            {
                Message message=new Message();
                message.what=5;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("music").equals("resume"))
            {
                Message message=new Message();
                message.what=6;
                handler.sendMessage(message);
            }
            //别人请求播放指定缩印的音乐
            else if(intent.getStringExtra("music").equals("playmusicindex"))
            {
                if(curPlayIndex != intent.getIntExtra("mp3index",0))
                {
                    curPlayIndex = intent.getIntExtra("mp3index", 0);
                    ismeadiaPlayerFirstPrepared = true;
                    Message message = new Message();
                    message.what = 7;
                    handler.sendMessage(message);
                }
            }
            else if(intent.getStringExtra("music").equals("mp3update"))
            {
                Message message=new Message();
                message.what=8;
                handler.sendMessage(message);
            }
        }
    }

    public class MusicBinder extends Binder
    {
        MusicPlayService getService()
        {
            return MusicPlayService.this;
        }
    }
}
