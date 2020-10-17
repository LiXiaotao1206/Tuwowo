package jingpinwu.android.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class PPWActivity extends Activity
{
    PPWView view;
    String gengxinurl = "";
    int shareflg = 0;

    // 調用系統方法分享文件
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                Uri fileURI = FileProvider.getUriForFile(context, "jingpinwu.android.com.fileprovider", file);
                share.putExtra(Intent.EXTRA_STREAM, fileURI);
            }
            else
            {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        }
        else
        {
            Toast.makeText(context,"分享失败",Toast.LENGTH_SHORT).show();
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,BGMService.class);
        intent.putExtra("MUSIC","WA");
        startService(intent);
        gengxinurl = getIntent().getExtras().getString("GENGXINURL").toString();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        view = new PPWView(this);
        setContentView(view);
    }

    @Override
    protected void onStop()
    {
        view.setgoflg(true);
        Intent intent = new Intent(this,BGMService.class);
        stopService(intent);
        view.setThflg(false);
        PPWActivity.this.finish();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        view.setgoflg(true);
        Intent intent = new Intent(this,BGMService.class);
        stopService(intent);
        view.setThflg(false);
        PPWActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(view.getshare()==true && shareflg == 0)
        {
            String[] strings = {"简单分享","高级分享"};
            new AlertDialog.Builder(this).setItems(strings, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    if(i==0)
                    {
                        String s = "我在兔喔喔皮皮蛙惊险挑战赛中获得成绩"+ view.getChengji()+ "分，过来一起挑战吧！下载地址："+gengxinurl;
                        share(s);
                    }
                    else
                    {
                        sharehtml();
                    }
                }
            }).create().show();
            shareflg = 1;
            return true;
        }
        return super.onTouchEvent(event);
    }

    void share(String s)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");
        this.startActivity(sendIntent);
    }

    void sharehtml()
    {
        String html = "";
        html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body bgcolor=\"#ffaa43\"><div class=\"content\"><div class=\"article_con\">";
        String p = "";
        p += "<p style=\"text-align: center;\"><br><br><br><br>我在兔喔喔皮皮蛙惊险挑战赛中获得成绩" + view.getChengji() + "分，过来一起挑战吧！<br><br><br><br></p>";
        p += "<p style=\"text-align: center;\"><input type=\"button\" style = \"center\" value=\"下载兔喔喔APP\" onclick=\"javascrtpt:window.location.href='" + gengxinurl + "'\"></p>";
        p += "<p style=\"text-align: center;\"<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></p><p></p><p></p><p></p><p></p><p></p>";
        html += p;
        html += "</div></body></html>";
        File file = new File("/sdcard/jingpinwu/我在皮皮蛙极限挑战赛得分" + view.getChengji() + "分，过来挑战吧.html");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        OutputStreamWriter write = null;
        try
        {
            write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        BufferedWriter writer = new BufferedWriter(write);
        try
        {
            writer.write(html);
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        shareFile(PPWActivity.this, file);
    }
}
