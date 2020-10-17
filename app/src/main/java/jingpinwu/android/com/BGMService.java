package jingpinwu.android.com;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by lixiaotao on 2018/5/17.
 */

public class BGMService extends Service
{
    MediaPlayer mediaPlayer ;
    String music = "";
    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        music = intent.getStringExtra("MUSIC").toString();
        if(music.contains("WA"))
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.kuai);
        }
        else if(music.contains("GUI"))
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.renzhemenggui);
        }
        else if(music.contains("GY"))
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.smmusic);
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
