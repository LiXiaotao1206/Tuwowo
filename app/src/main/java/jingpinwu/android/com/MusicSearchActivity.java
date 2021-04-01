package jingpinwu.android.com;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MusicSearchActivity extends AppCompatActivity
{
    private ViewPager viewPager;
    private FragmentMusic fragmentMusicMG;
    private FragmentMusic fragmentMusicKW;
    private FragmentMusic fragmentMusicWY;
    private TextView tvKW;
    private TextView tvWY;
    private TextView tvMG;
    private MyImageView imgMusicPlay;
    private TextView tvSongName;
    private MyBroadcastReceiver broadcastReceiver;
    private App app;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 0:
                    app.mp3DataList.clear();
                    app.mp3DataList.addAll(app.wyMp3DataList);
                    Intent intent0 = new Intent();
                    intent0.setAction("MUSIC_IN");
                    intent0.putExtra("music","mp3update");
                    sendBroadcast(intent0);
                    break;
                case 1:
                    app.mp3DataList.clear();
                    app.mp3DataList.addAll(app.mgMp3DataList);
                    Intent intent1 = new Intent();
                    intent1.setAction("MUSIC_IN");
                    intent1.putExtra("music","mp3update");
                    sendBroadcast(intent1);
                    break;
                case 2:
                    app.mp3DataList.clear();
                    app.mp3DataList.addAll(app.kwMp3DataList);
                    Intent intent2 = new Intent();
                    intent2.setAction("MUSIC_IN");
                    intent2.putExtra("music","mp3update");
                    sendBroadcast(intent2);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_music_main);

        app = (App)getApplication();

        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction("MUSIC_OUT");
        // 3. 动态注册：调用Context的registerReceiver（）方法
        registerReceiver(broadcastReceiver,intentFilter);

        Intent intent_service = new Intent(this,MusicPlayService.class);
        startService(intent_service);

        viewPager = (ViewPager)findViewById(R.id.viewpager_music);
        List<Fragment> list = new ArrayList<>();
        fragmentMusicMG = new FragmentMusic();
        fragmentMusicKW = new FragmentMusic();
        fragmentMusicWY = new FragmentMusic();
        fragmentMusicMG.setSource("migu");
        fragmentMusicKW.setSource("kugou");
        fragmentMusicWY.setSource("netease");

        list.add(fragmentMusicWY);
        list.add(fragmentMusicMG);
        list.add(fragmentMusicKW);

        PagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(pagerAdapter);

        final EditText etSearch = (EditText)findViewById(R.id.music_edittext_search);
        Button btnSearch = (Button)findViewById(R.id.music_btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(etSearch.getText().length()<1)
                {
                    Toast.makeText(MusicSearchActivity.this,"请输入关键字！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(viewPager.getCurrentItem()==0)
                {
                    fragmentMusicWY.search(etSearch.getText().toString());
                }
                else if(viewPager.getCurrentItem()==1)
                {
                    fragmentMusicMG.search(etSearch.getText().toString());
                }
                else
                {
                    fragmentMusicKW.search(etSearch.getText().toString());
                }
                hideInput();
                etSearch.clearFocus();
            }
        });

        tvMG = (TextView)findViewById(R.id.music_top_mg);
        tvKW = (TextView)findViewById(R.id.music_top_kw);
        tvWY = (TextView)findViewById(R.id.music_top_wy);
        imgMusicPlay = (MyImageView) findViewById(R.id.music_btn_img);
        tvSongName = (TextView)findViewById(R.id.music_btn_songname);
        tvSongName.setSelected(true);
        tvSongName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(app.mp3DataList.size()<1)
                {
                    Toast.makeText(MusicSearchActivity.this,"请重新选择音乐播放！",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MusicSearchActivity.this,MusicPlayActivity.class);
                startActivity(intent);
            }
        });
        imgMusicPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(app.mp3DataList.size()<1)
                {
                    Toast.makeText(MusicSearchActivity.this,"请重新选择音乐播放！",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MusicSearchActivity.this,MusicPlayActivity.class);
                startActivity(intent);
            }
        });
        tvMG.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(1);
                tvMG.setTextSize(23);
                tvMG.setTextColor(Color.rgb(255,255,255));
                tvMG.getPaint().setFakeBoldText(true);
                tvKW.setTextSize(20);
                tvKW.setTextColor(Color.rgb(213,213,213));
                tvKW.getPaint().setFakeBoldText(false);
                tvWY.setTextSize(20);
                tvWY.setTextColor(Color.rgb(213,213,213));
                tvWY.getPaint().setFakeBoldText(false);

                resetMusicList(1);
            }
        });

        tvKW.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(2);
                tvMG.setTextSize(20);
                tvMG.setTextColor(Color.rgb(213,213,213));
                tvMG.getPaint().setFakeBoldText(false);
                tvKW.setTextSize(23);
                tvKW.setTextColor(Color.rgb(255,255,255));
                tvKW.getPaint().setFakeBoldText(true);
                tvWY.setTextSize(20);
                tvWY.setTextColor(Color.rgb(213,213,213));
                tvWY.getPaint().setFakeBoldText(false);

                resetMusicList(2);
            }
        });
        tvWY.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(0);
                tvMG.setTextSize(20);
                tvMG.setTextColor(Color.rgb(213,213,213));
                tvMG.getPaint().setFakeBoldText(false);
                tvKW.setTextSize(20);
                tvKW.setTextColor(Color.rgb(213,213,213));
                tvKW.getPaint().setFakeBoldText(false);
                tvWY.setTextSize(23);
                tvWY.setTextColor(Color.rgb(255,255,255));
                tvWY.getPaint().setFakeBoldText(true);

                resetMusicList(0);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position==1)
                {
                    tvMG.setTextSize(23);
                    tvMG.setTextColor(Color.rgb(255,255,255));
                    tvMG.getPaint().setFakeBoldText(true);
                    tvKW.setTextSize(20);
                    tvKW.setTextColor(Color.rgb(213,213,213));
                    tvKW.getPaint().setFakeBoldText(false);
                    tvWY.setTextSize(20);
                    tvWY.setTextColor(Color.rgb(213,213,213));
                    tvWY.getPaint().setFakeBoldText(false);
                }
                else if(position==2)
                {
                    tvMG.setTextSize(20);
                    tvMG.setTextColor(Color.rgb(213,213,213));
                    tvMG.getPaint().setFakeBoldText(false);
                    tvKW.setTextSize(23);
                    tvKW.setTextColor(Color.rgb(255,255,255));
                    tvKW.getPaint().setFakeBoldText(true);
                    tvWY.setTextSize(20);
                    tvWY.setTextColor(Color.rgb(213,213,213));
                    tvWY.getPaint().setFakeBoldText(false);
                }
                else if(position==0)
                {
                    tvMG.setTextSize(20);
                    tvMG.setTextColor(Color.rgb(213,213,213));
                    tvMG.getPaint().setFakeBoldText(false);
                    tvKW.setTextSize(20);
                    tvKW.setTextColor(Color.rgb(213,213,213));
                    tvKW.getPaint().setFakeBoldText(false);
                    tvWY.setTextSize(23);
                    tvWY.setTextColor(Color.rgb(255,255,255));
                    tvWY.getPaint().setFakeBoldText(true);
                }
                resetMusicList(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

    }

    void resetMusicList(int curPageIndex)
    {
        Toast.makeText(this,"当前列表已设为默认播放列表！",Toast.LENGTH_SHORT).show();
        Message message = new Message();
        message.what = curPageIndex;
        handler.sendMessage(message);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v)
        {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy()
    {
        Intent intent_service = new Intent(this,MusicPlayService.class);
        stopService(intent_service);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        //Intent intent = new Intent();
        //intent.setAction("MUSIC_IN");
        //intent.putExtra("music","stop");
        //sendBroadcast(intent);
        super.onStop();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getStringExtra("music").equals("curindexchange"))
            {
                //此处在resume 和 prepare都会各收到一次，因此，有可能会收到2次
                int curPlay = intent.getIntExtra("curplay",0);
                if(curPlay<app.mp3DataList.size() && curPlay>=0)
                {
                    imgMusicPlay.setImageURL(app.mp3DataList.get(curPlay).picurl);
                    tvSongName.setText(app.mp3DataList.get(curPlay).title);
                }
            }
        }
    }
}
