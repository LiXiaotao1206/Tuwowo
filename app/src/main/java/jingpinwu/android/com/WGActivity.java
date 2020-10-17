package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by lixiaotao on 2018/4/19.
 */

public class WGActivity extends Activity
{
    WGView view;
    String gengxinurl = "";
    int shareflg = 0;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,BGMService.class);
        intent.putExtra("MUSIC","GUI");
        startService(intent);
        gengxinurl = getIntent().getExtras().getString("GENGXINURL").toString();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        view = new WGView(this);
        view.setGengxinUrl("http://slark.ys168.com/");
        view.setClickable(true);
        setContentView(view);
    }

    @Override
    protected void onStop()
    {
        view.setgoflg(true);
        Intent intent = new Intent(this,BGMService.class);
        stopService(intent);
        view.setThflg(false);
        WGActivity.this.finish();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        view.setgoflg(true);
        Intent intent = new Intent(this,BGMService.class);
        stopService(intent);
        view.setThflg(false);
        WGActivity.this.finish();
        super.onBackPressed();
    }

}
