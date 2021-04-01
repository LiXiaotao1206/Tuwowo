package jingpinwu.android.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DSActivity extends Activity
{
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashang);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        TextView textView = (TextView)findViewById(R.id.textView_dashang);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button button = (Button)findViewById(R.id.btn_dashang);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri CONTENT_URI_BROWSERS = Uri.parse("https://qr.alipay.com/fkx19797lv52hlggpne8y32?t=1608098194375");
                intent.setData(CONTENT_URI_BROWSERS);
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                final Activity context = DSActivity.this;
                try
                {
                    context.startActivity(intent);
                }
                catch (Exception e)
                {
                    Intent intent2=new Intent();
                    intent2.setAction("android.intent.action.VIEW");
                    intent2.setData(CONTENT_URI_BROWSERS);
                    try {
                        Toast.makeText(DSActivity.this,"请选择手机内置浏览器！",Toast.LENGTH_LONG).show();
                        context.startActivity(intent2);
                    }
                    catch (Exception e2) {
                        new AlertDialog.Builder(context)
                                .setMessage("未检测到支付宝客户端，请安装后重试。")
                                .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://d.alipay.com")));
                                    }
                                }).setNegativeButton("取消", null).show();
                    }
                }
            }
        });
    }
}
