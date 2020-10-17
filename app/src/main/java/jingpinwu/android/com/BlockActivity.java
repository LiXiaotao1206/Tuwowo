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
import android.view.KeyEvent;
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
import java.text.DecimalFormat;

/**
 * Created by lixiaotao on 2018/4/19.
 */

public class BlockActivity extends Activity
{
    BlockView view;
    String gengxinurl = "";

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gengxinurl = getIntent().getExtras().getString("GENGXINURL").toString();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        view = new BlockView(this);
        setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        view.setThflg(false);
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        BlockActivity.this.finish();
        return false;
       // if (keyCode == KeyEvent.KEYCODE_BACK)
        //{
           // return false;
       // }
       // return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(view.getExit())
        {
            view.setThflg(false);
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            BlockActivity.this.finish();
        }
        else if(view.getcanManfen())
        {
            view.setcanManfen(false);


            String[] strings = {"简单分享","高级分享"};
            new AlertDialog.Builder(this).setItems(strings, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    DecimalFormat df;
                    if(view.getTime()>=100)
                    {
                        df = new DecimalFormat("000.000");
                    }
                    else if(view.getTime()>=10)
                    {
                        df = new DecimalFormat("00.000");
                    }
                    else
                    {
                        df = new DecimalFormat("0.000");
                    }
                    if(i==0)
                    {

                        Intent intent_share = new Intent(Intent.ACTION_SEND);
                        intent_share.putExtra(Intent.EXTRA_TEXT, "我在兔喔喔百步钢琴赛中完成100步只用了" + df.format(view.getTime()) + "秒，敢来兔喔喔跟我决一雌雄吗\r\n下载：" + gengxinurl+"  \r\n--兔喔喔");
                        intent_share.setType("text/plain");
                        startActivity(Intent.createChooser(intent_share, "分享至"));
                    }
                    else
                    {
                        String html = "";
                        html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body bgcolor=\"#ffaa43\"><div class=\"content\"><div class=\"article_con\">";
                        String p = "";
                        p+="<p style=\"text-align: center;\"><br><br><br><br>我在兔喔喔百步钢琴赛中挑战极限只用了"+df.format(view.getTime())+"秒，过来一起挑战吧！<br><br><br><br></p>";
                        p+="<p style=\"text-align: center;\"><input type=\"button\" style = \"center\" value=\"下载兔喔喔APP\" onclick=\"javascrtpt:window.location.href='"+gengxinurl+"'\"></p>";
                        p+="<p style=\"text-align: center;\"<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></p><p></p><p></p><p></p><p></p><p></p>";
                        html += p;
                        html+="</div></body></html>";
                        File file = new File("/sdcard/jingpinwu/我在百步钢琴挑战用时"+df.format(view.getTime())+"秒，过来挑战吧.html");
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
                        BufferedWriter writer = null;
                        try
                        {
                            write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                            writer = new BufferedWriter(write);
                        } catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        } catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                        try
                        {
                            writer.write(html);
                            writer.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        shareFile(BlockActivity.this,file);
                    }
                }
            }).create().show();
        }
        return false;
        //return super.onTouchEvent(event);
    }
}
