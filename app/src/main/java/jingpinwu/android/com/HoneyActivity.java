package jingpinwu.android.com;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by 李晓涛 on 2018/2/20.
 */

public class HoneyActivity extends AppCompatActivity
{
    String honeystr="\r\n\r\n\r\n兔喔喔\r\n为您提供精彩内容\r\n本应用所有资源来源于网络\r\n如有侵权请发送邮件至以下邮箱\r\ntuwowo@aliyun.com\r\n我们将及时删除对应内容\r\n以保护您的权益\r\n\r\n开发者：李晓涛(Slark)";
    EditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_honey);

        editText = (EditText)findViewById(R.id.editTexthoney);
        editText.setText(honeystr);
    }
}
