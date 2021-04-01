package jingpinwu.android.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DouyuMainActivity extends AppCompatActivity
{
    ImageView wangyoujingji;
    ImageView danjireyou;
    ImageView shouyou;
    ImageView yule;
    ImageView yanzhi;
    ImageView kejijiaoyu;
    ImageView yuyinhudong;
    ImageView zhengnengliang;
    ImageView gengduoneirong;
    View.OnClickListener listenner = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int cate1Id = 1;
            switch(view.getId())
            {
                case R.id.dy_wyjj:
                    cate1Id = 1;
                    break;
                case R.id.dy_djry:
                    cate1Id = 15;
                    break;
                case R.id.dy_syxx:
                    cate1Id = 9;
                    break;
                case R.id.dy_yltd:
                    cate1Id = 2;
                    break;
                case R.id.dy_yz:
                    cate1Id = 8;
                    break;
                case R.id.dy_kjjy:
                    cate1Id = 11;
                    break;
                case R.id.dy_yyhd:
                    cate1Id = 18;
                    break;
                case R.id.dy_znl:
                    cate1Id = 13;
                    break;
                case R.id.dy_more:
                    Toast.makeText(DouyuMainActivity.this,"更多精彩，等待更新！",Toast.LENGTH_LONG).show();
                    return;
                default:
                    return;
            }
            Intent intent = new Intent(DouyuMainActivity.this,DouyuSecondActivity.class);
            intent.putExtra("cate1Id", cate1Id);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_douyu);
        wangyoujingji = (ImageView)findViewById(R.id.dy_wyjj);
        danjireyou = (ImageView)findViewById(R.id.dy_djry);
        shouyou = (ImageView)findViewById(R.id.dy_syxx);
        yule = (ImageView)findViewById(R.id.dy_yltd);
        yanzhi = (ImageView)findViewById(R.id.dy_yz);
        kejijiaoyu = (ImageView)findViewById(R.id.dy_kjjy);
        yuyinhudong = (ImageView)findViewById(R.id.dy_yyhd);
        zhengnengliang = (ImageView)findViewById(R.id.dy_znl);
        gengduoneirong = (ImageView)findViewById(R.id.dy_more);

        wangyoujingji.setOnClickListener(listenner);
        danjireyou.setOnClickListener(listenner);
        shouyou.setOnClickListener(listenner);
        yule.setOnClickListener(listenner);
        yanzhi.setOnClickListener(listenner);
        kejijiaoyu.setOnClickListener(listenner);
        yuyinhudong.setOnClickListener(listenner);
        zhengnengliang.setOnClickListener(listenner);
        gengduoneirong.setOnClickListener(listenner);

    }
}
