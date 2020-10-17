package jingpinwu.android.com;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * Created by 李晓涛 on 2018/2/20.
 */

public class GuanyuActivity extends Activity
{
    EditText editText;
    String versionName="";
    PackageManager packageManager;
    PackageInfo packageInfo;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.layout_guanyu);
        packageManager=getApplicationContext().getPackageManager();
        try
        {
            packageInfo=packageManager.getPackageInfo(getApplicationContext().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e)
        {
           // e.printStackTrace();
        }
        versionName=packageInfo.versionName;
        editText = (EditText)findViewById(R.id.editTextshuoming);
        String putongstr="\r\n\r\n兔喔喔\r\n当前版本 "+versionName+"\r\n感谢兔迷的厚爱与支持\r\n正因其提出的宝贵建议\r\n才有了兔喔喔愈发精致的品性\n兔喔喔经过长期用心的积累\r\n获得了一大批忠实的粉丝\r\n欢迎大家注册会员\r\n积极留言提出建议\r\n也可以将建议发送至兔喔喔开发团队邮箱\r\ntuwowo@aliyun.com\r\n我们会积极升级改进\r\n这将是兔喔喔不断前进的动力\r\n同时特别致谢以下两位BIGGOD\r\nL鑫  H喆\r\n为兔喔喔的开发做出重大贡献\r\n\r\nby Slark\r\n李涛";

        editText.setText(putongstr);
        ImageView imageView = (ImageView)findViewById(R.id.img_musickaiguan);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(GuanyuActivity.this,BGMService.class);
                stopService(intent);
            }
        });
        Intent intent = new Intent(this,BGMService.class);
        intent.putExtra("MUSIC","GY");
        startService(intent);
    }
    @Override
    protected void onPause()
    {
        Intent intent = new Intent(this,BGMService.class);
        stopService(intent);
        super.onPause();
    }
}
