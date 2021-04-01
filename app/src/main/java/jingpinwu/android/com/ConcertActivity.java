package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Created by lixiaotao on 2018/4/19.
 */

public class ConcertActivity extends Activity
{
    ConcertView view;
    //String gengxinurl = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //gengxinurl = getIntent().getExtras().getString("GENGXINURL").toString();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        view = new ConcertView(this,"兔喔喔", 2,Color.YELLOW);
        view.setGo(true);

        final EditText et = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false).setTitle("请输入文字：").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(et.getText().toString().length()<1)
                {
                    view.setText("兔喔喔");
                }
                else
                {
                    view.setText(et.getText().toString());
                }
                String[] color = {"红色","绿色","黄色"};
                new AlertDialog.Builder(ConcertActivity.this).setCancelable(false).setItems(color, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String[] s = {"大","中","小"};
                        switch (i)
                        {
                            case 0:
                                view.setColor(Color.RED);
                                new AlertDialog.Builder(ConcertActivity.this).setCancelable(false).setTitle("字体大小").setItems(s, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        view.setSize(i);
                                        view.setLength();
                                    }
                                }).create().show();
                                break;
                            case 1:
                                view.setColor(Color.GREEN);
                                new AlertDialog.Builder(ConcertActivity.this).setCancelable(false).setTitle("字体大小").setItems(s, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        view.setSize(i);
                                        view.setLength();
                                    }
                                }).create().show();
                                break;
                            case 2:
                                view.setColor(Color.YELLOW);
                                new AlertDialog.Builder(ConcertActivity.this).setCancelable(false).setTitle("字体大小").setItems(s, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        view.setSize(i);
                                        view.setLength();
                                    }
                                }).create().show();
                                break;
                            default:
                                view.setColor(Color.RED);
                                new AlertDialog.Builder(ConcertActivity.this).setCancelable(false).setTitle("字体大小").setItems(s, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        view.setSize(i);
                                        view.setLength();
                                    }
                                }).create().show();
                                break;
                        }

                    }
                }).create().show();
            }
        });
        builder.create().show();
        setContentView(view);
    }
    public String StrFanzhuan(String str)
    {
        String s = "";
        for (int i = str.length() - 1; i >= 0; i--)
        {
            char c = str.charAt(i);
            s += c;
        }
        return s;
    }


    @Override
    protected void onStop()
    {
        view.setGo(false);
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        view.setGo(false);
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        ConcertActivity.this.finish();
        return false;
    }
}
