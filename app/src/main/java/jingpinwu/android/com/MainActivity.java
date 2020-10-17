package jingpinwu.android.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.os.Process;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;


import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    AlertDialog alertDialog;
    View.OnLongClickListener main_long_listener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            if(v.getId()==R.id.img_main_video)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("专业搜索：");
                View view1 = View.inflate(MainActivity.this,R.layout.main,null);
                final TextView editText_tuwowo = (TextView) view1.findViewById(R.id.maintext_tuwowo);
                final TextView editText_coredebug = (TextView) view1.findViewById(R.id.maintext_coredebug);
                final TextView editText_tengxun = (TextView) view1.findViewById(R.id.maintext_tengxun);
                final TextView editText_youku = (TextView) view1.findViewById(R.id.maintext_youku);
                final TextView editText_aiqiyi = (TextView) view1.findViewById(R.id.maintext_aiqiyi);
                final ImageView img_tuwowo = (ImageView)view1.findViewById(R.id.mainimg_tuwowo);
                final ImageView img_coredebug = (ImageView)view1.findViewById(R.id.mainimg_coredebug);
                final ImageView img_tengxun = (ImageView)view1.findViewById(R.id.mainimg_tengxun);
                final ImageView img_youku = (ImageView)view1.findViewById(R.id.mainimg_youku);
                final ImageView img_aiqiyi = (ImageView)view1.findViewById(R.id.mainimg_aiqiyi);
                builder.setView(view1);
                final AlertDialog alertDialog = builder.create();
                editText_tuwowo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, JiexiActivity.class);
                        intent.putExtra("SEARCH", "http://m.v.sogou.com/film/list/");
                        startActivity(intent);
                    }
                });
                editText_coredebug.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this,"此属于敏感操作，不懂不要瞎搞啊！",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://debugtbs.qq.com");
                        startActivity(intent);
                    }
                });
                editText_tengxun.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://film.qq.com/weixin/all.html");
                        startActivity(intent);
                    }
                });
                editText_youku.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","https://vip.youku.com/vips/index.html");
                        startActivity(intent);
                    }
                });
                editText_aiqiyi.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://m.iqiyi.com/vip/#0");
                        startActivity(intent);
                    }
                });
                img_tuwowo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, JiexiActivity.class);
                        intent.putExtra("SEARCH", "http://m.v.sogou.com/film/list/");
                        startActivity(intent);
                    }
                });
                img_coredebug.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this,"此属于敏感操作，不懂不要瞎搞啊！",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://debugtbs.qq.com");
                        startActivity(intent);
                    }
                });
                img_tengxun.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://film.qq.com/weixin/all.html");
                        startActivity(intent);
                    }
                });
                img_youku.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","https://vip.youku.com/vips/index.html");
                        startActivity(intent);
                    }
                });
                img_aiqiyi.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                        intent.putExtra("SEARCH","http://m.iqiyi.com/vip/#0");
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
            return false;
        }
    };
    private ImageView img_video;
    private ImageView img_music;
    private ImageView img_zhibo;
    private ImageView img_douyu;
    private ImageView img_xiaoshuo;
    private ImageView img_datasheet;
    private ImageView img_xiaofei;
    private ImageView img_zimu;
    private ImageView img_renren;
    private ImageView img_zhoukan;
    private ImageView img_pipiwa;
    private ImageView img_gangqinshou;
    private ImageView img_wugui;
    private ImageView img_more;
    private ListView listView_set;
    private List<Wangzhan> wangzhanList = new ArrayList<Wangzhan>();
    private WangzhanAdapter wangzhanadapter;
    private ProgressDialog progressDialog;
    private EditText editText_search;
    private Button button_search;
    private Button button_search_all;
    private Timer timer1;
    // 底部菜单4个Linearlayout
    private LinearLayout tab_read;
    private LinearLayout tab_think;
    private LinearLayout tab_settings;
    // 底部菜单4个ImageView
    private ImageView img_read;
    private ImageView img_think;
    private ImageView img_settings;
    // 底部菜单4个菜单标题
    private TextView tv_read;
    private TextView tv_think;
    private TextView tv_settings;
    // 中间内容区域
    private ViewPager viewPager;
    View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
            buttonInit();
            switch (view.getId())
            {
                case R.id.tab_read:
                    img_read.setImageResource(R.drawable.read_blue);
                    tv_read.setTextColor(getColor(R.color.colorPrimaryDark));
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.tab_think:
                    img_think.setImageResource(R.drawable.search_blue);
                    tv_think.setTextColor(getColor(R.color.colorPrimaryDark));
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.tab_settings:
                    img_settings.setImageResource(R.drawable.set_blue);
                    tv_settings.setTextColor(getColor(R.color.colorPrimaryDark));
                    viewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }
        }
    };
    // ViewPager适配器ContentAdapter
    private ContentAdapter adapter;
    private List<View> views;
    private  boolean isoversetinit=false;
    private int netInfo;
    private  String gengxinurl="";
    private  String leixing="";
    View.OnClickListener main_listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.img_main_video:
                    AlertDialog.Builder builder2;
                    View view1;
                    builder2 = new android.app.AlertDialog.Builder(MainActivity.this);
                    view1 = View.inflate(MainActivity.this,R.layout.jiexixuanze,null);
                    final ImageView imageView_ku1 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu1);
                    final ImageView imageView_ku2 = (ImageView)view1.findViewById(R.id.jiexi_img_xianlu2);
                    imageView_ku1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent1 = new Intent(MainActivity.this, KWActivity.class);
                            intent1.putExtra("SEARCH", "http://vtyyy.com/index.php/vod/search.html?wd=%E7%94%B5%E8%A7%86%E5%89%A7");
                            startActivity(intent1);
                            alertDialog.dismiss();
                        }
                    });
                    imageView_ku2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent1 = new Intent(MainActivity.this, JiexiActivity.class);
                            intent1.putExtra("SEARCH", "http://m.v.sogou.com/teleplay/list/");
                            startActivity(intent1);
                            alertDialog.dismiss();
                        }
                    });
                    builder2.setView(view1);
                    alertDialog = builder2.create();
                    alertDialog.show();
                    break;
                case R.id.img_main_music:
                    Intent intent2 = new Intent(MainActivity.this,MusicSearchActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.img_main_xiaoshuo:
                    Intent intent3 = new Intent(MainActivity.this,XiaoshuoListActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.img_main_zhibo:
                    Intent intent4 = new Intent(MainActivity.this,ZB_Activity.class);
                    startActivity(intent4);
                    break;

                case R.id.img_main_datasheet:
                    Intent intent5 = new Intent(MainActivity.this,DataSheetActivity.class);
                    intent5.putExtra("DATASHEET","http://datasheet.eeworld.com.cn/part/IRD-LPC1768-DEV,Future%20Designs%20Inc,7962931.html");
                    startActivity(intent5);
                    break;
                case R.id.img_main_xiaofei:
                    Intent intent6 = new Intent(MainActivity.this, feifeiActivity.class);
                    startActivity(intent6);
                    break;
                case R.id.img_main_concert:
                    Intent intent7 = new Intent(MainActivity.this,ConcertActivity.class);
                    startActivity(intent7);
                    break;
                case R.id.img_main_jianshu:
                    Intent intent8 = new Intent(MainActivity.this,JianshuActivity.class);
                    intent8.putExtra("GENGXINURL",gengxinurl);
                    startActivity(intent8);
                    break;
                case R.id.img_main_zhoukan:
                    final String[] items = new String[]{"精选整合","人工智能", "移动互联", "科技产品", "人物报道", "移动应用"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("请选择类型：").setItems(items, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            switch (i)
                            {
                                case 0:
                                    Intent intent9 = new Intent(MainActivity.this,BTListActivity.class);
                                    intent9.putExtra("GENGXINURL",gengxinurl);
                                    startActivity(intent9);
                                    return;
                                case 1:
                                    leixing = "ai";
                                    break;
                                case 2:
                                    leixing = "mi";
                                    break;
                                case 3:
                                    leixing = "mo";
                                    break;
                                case 4:
                                    leixing = "pp";
                                    break;
                                case 5:
                                    leixing = "ap";
                                    break;
                                default:
                                    break;
                            }
                            Intent intent10 = new Intent(MainActivity.this, BetaListActivity.class);
                            intent10.putExtra("LEIXING", leixing);
                            intent10.putExtra("GENGXINURL", gengxinurl);
                            startActivity(intent10);
                        }
                    }).create().show();
                    break;
                case R.id.img_main_pipiwa:
                    Intent intent = new Intent(MainActivity.this, PPWActivity.class);
                    intent.putExtra("GENGXINURL", gengxinurl);
                    startActivity(intent);
                    break;
                case R.id.img_main_gangqinshou:
                    Intent intent11 = new Intent(MainActivity.this, BlockActivity.class);
                    intent11.putExtra("GENGXINURL", gengxinurl);
                    startActivity(intent11);
                    break;
                case R.id.img_main_wugui:
                    Intent intent12 = new Intent(MainActivity.this, WGActivity.class);
                    intent12.putExtra("GENGXINURL", gengxinurl);
                    startActivity(intent12);
                    break;
                case R.id.img_main_douyu:
                    Intent intent13 = new Intent(MainActivity.this,DouyuMainActivity.class);
                    startActivity(intent13);
                    break;
                default:
                    Toast.makeText(MainActivity.this,"更多精彩，等待更新...",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private  String gengxintxt = "";
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                {
                    readInit();
                    tab_read.performClick();
                    break;
                }
                case 2:
                {
                    tab_think.performClick();
                    break;
                }
                case 3:
                {
                    tab_think.performClick();
                    break;
                }
                case 4:
                {
                    break;
                }
                case 100:
                {
                    WriteGengxinFile();
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    TimerTask timerTask_go_read=new TimerTask()
    {
        @Override
        public void run()
        {
            Message message=new Message();
            message.what=1;
            handler.sendMessage(message);
        }
    };
    private String isgengxin = "yes";
    private File file = new File("/sdcard/jingpinwu");
    private long totalsize = 0;
    ViewPager.OnPageChangeListener listenerpage = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            buttonInit();
            switch (position)
            {
                case 0:
                    img_read.setImageResource(R.drawable.read_blue);
                    tv_read.setTextColor(getColor(R.color.colorPrimaryDark));
                    readInit();
                    break;
                case 1:
                    img_think.setImageResource(R.drawable.search_blue);
                    tv_think.setTextColor(getColor(R.color.colorPrimaryDark));
                    thinkInit();
                    break;
                case 2:
                    img_settings.setImageResource(R.drawable.set_blue);
                    tv_settings.setTextColor(getColor(R.color.colorPrimaryDark));
                    setInit();
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };
    private long cachesize=0;
    private long datasize=0;
    private long codesize=0;
    private MyBroadcastReceiver myBroadcastReceiver;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getCacheSizeByAndroidO(Context mcontext, String mPackageName)
    {
        StorageManager storageManager = (StorageManager)mcontext.getSystemService(Context.STORAGE_SERVICE);
        StorageStatsManager storageStatsManager = (StorageStatsManager)mcontext.getSystemService(Context.STORAGE_STATS_SERVICE);
        try
        {
            StorageStats storageStats = storageStatsManager.queryStatsForPackage(StorageManager.UUID_DEFAULT,mPackageName,Process.myUserHandle());
            return storageStats.getCacheBytes()+storageStats.getDataBytes();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    protected void onResume()
    {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction("TAB");

        // 3. 动态注册：调用Context的registerReceiver（）方法
        registerReceiver(myBroadcastReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        //销毁在onResume()方法中的广播
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //gengxinurl = this.getIntent().getExtras().getString("GENGXINURL").toString();
        gengxinurl = "http://slark.ys168.com/";
        isgengxin = this.getIntent().getExtras().getString("GENGXINFLG").toString();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("读取服务器数据中...");
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if(progressDialog.isShowing())
                    {
                        progressDialog.setCancelable(true);
                        progressDialog.dismiss();
                    }
                }
                return false;
            }
        });

        viewInit();
        eventInit();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)//版本小于安卓8.0
        {
            try
            {
                queryPacakgeSize("jingpinwu.android.com");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else//版本大于等于安卓8.0
        {
            totalsize = getCacheSizeByAndroidO(MainActivity.this,"jingpinwu.android.com");
        }
        timer1 = new Timer();
        timer1.schedule(timerTask_go_read, 50);

        App app = (App)getApplication();
        if(!app.x5InitFlg)
        {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("提示")
                    .setMessage("腾讯内核加载失败，请打开数据连接或者WIFI网络并按照以下步骤操作\r\n\r\n1.选择“重新加载内核”\r\n2.选择“清除TBS内核”\r\n3.选择“安装线上内核”\r\n4.重启APP")
                    .setPositiveButton("重新加载内核", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent(MainActivity.this,LiulanActivity.class);
                            intent.putExtra("SEARCH","http://debugtbs.qq.com");
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            MainActivity.this.finish();
                        }
                    })
                    .create().show();
        }

    }

    public void viewInit()
    {
        //底部菜单3个linearlayout
        this.tab_read = (LinearLayout)findViewById(R.id.tab_read);
        this.tab_think = (LinearLayout)findViewById(R.id.tab_think);
        this.tab_settings = (LinearLayout)findViewById(R.id.tab_settings);

        // 底部菜单3个ImageView
        this.img_read = (ImageView) findViewById(R.id.id_tab_read_img);
        this.img_think = (ImageView) findViewById(R.id.id_tab_search_img);
        this.img_settings = (ImageView) findViewById(R.id.id_tab_settings_img);

        //底部菜单文字3个TextView
        this.tv_read = (TextView)findViewById(R.id.id_tv_read);
        this.tv_think = (TextView)findViewById(R.id.id_tv_think);
        this.tv_settings = (TextView)findViewById(R.id.id_tv_settings);
        // 中间内容区域ViewPager
        this.viewPager = (ViewPager) findViewById(R.id.vp_content);

        // 适配器
        View page_01 = View.inflate(MainActivity.this, R.layout.layout_search, null);
        View page_02 = View.inflate(MainActivity.this, R.layout.layout_think_content, null);
        View page_03 = View.inflate(MainActivity.this, R.layout.layout_settings, null);

        views = new ArrayList<View>();
        views.add(page_01);
        views.add(page_02);
        views.add(page_03);
        this.adapter = new ContentAdapter(views);
        viewPager.setAdapter(adapter);
    }

    public void eventInit()
    {
        // 设置按钮监听
        tab_read.setOnClickListener(listener);
        tab_think.setOnClickListener(listener);
        tab_settings.setOnClickListener(listener);
        //设置ViewPager滑动监听
        viewPager.setOnPageChangeListener(listenerpage);

    }

    //底部按钮和文字初始化
    public void buttonInit()
    {
        img_read.setImageResource(R.drawable.readb);
        img_think.setImageResource(R.drawable.searchb);
        img_settings.setImageResource(R.drawable.setb);
        tv_read.setTextColor(Color.GRAY);
        tv_think.setTextColor(Color.GRAY);
        tv_settings.setTextColor(Color.GRAY);
    }

    public void thinkInit()
    {
        img_video = (ImageView)findViewById(R.id.img_main_video);
        img_music = (ImageView)findViewById(R.id.img_main_music);
        img_zhibo = (ImageView)findViewById(R.id.img_main_zhibo);
        img_douyu = (ImageView)findViewById(R.id.img_main_douyu);
        img_xiaoshuo = (ImageView)findViewById(R.id.img_main_xiaoshuo);
        img_datasheet = (ImageView)findViewById(R.id.img_main_datasheet);
        img_xiaofei = (ImageView)findViewById(R.id.img_main_xiaofei);
        img_zimu= (ImageView)findViewById(R.id.img_main_concert);
        img_renren = (ImageView)findViewById(R.id.img_main_jianshu);
        img_zhoukan = (ImageView)findViewById(R.id.img_main_zhoukan);
        img_pipiwa = (ImageView)findViewById(R.id.img_main_pipiwa);
        img_gangqinshou = (ImageView)findViewById(R.id.img_main_gangqinshou);
        img_wugui = (ImageView)findViewById(R.id.img_main_wugui);
        img_more = (ImageView)findViewById(R.id.img_main_more);

        img_video.setOnClickListener(main_listener);
        img_music.setOnClickListener(main_listener);
        img_zhibo.setOnClickListener(main_listener);
        img_douyu.setOnClickListener(main_listener);
        img_xiaoshuo.setOnClickListener(main_listener);
        img_datasheet.setOnClickListener(main_listener);
        img_xiaofei.setOnClickListener(main_listener);
        img_zimu.setOnClickListener(main_listener);
        img_renren.setOnClickListener(main_listener);
        img_zhoukan.setOnClickListener(main_listener);
        img_pipiwa.setOnClickListener(main_listener);
        img_gangqinshou.setOnClickListener(main_listener);
        img_wugui.setOnClickListener(main_listener);
        img_more.setOnClickListener(main_listener);
        img_video.setOnLongClickListener(main_long_listener);

    }

    public void readInit()
    {
        button_search = (Button) findViewById(R.id.button_search);
        button_search_all = (Button) findViewById(R.id.button_search_all);
        editText_search = (EditText)findViewById(R.id.edit_videosearch);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                editText_search.clearFocus();
                hideInput();

                if (editText_search.getText().toString().length() < 1)
                {
                    Toast.makeText(MainActivity.this, "请输入视频搜索关键字", Toast.LENGTH_SHORT).show();
                } else
                {
                    Intent intent = new Intent(MainActivity.this, JiexiActivity.class);
                    intent.putExtra("SEARCH", "http://m.v.sogou.com/v?query=" + editText_search.getText().toString());
                    startActivity(intent);
                }
            }
        });
        button_search_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                editText_search.clearFocus();
                hideInput();
                if (editText_search.getText().toString().length() < 1)
                {
                    Toast.makeText(MainActivity.this, "请输入视频搜索关键字", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, KWActivity.class);
                    intent.putExtra("SEARCH", editText_search.getText().toString());
                    startActivity(intent);
                }

            }
        });

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

    public  void setInit()
    {
        listView_set = (ListView)findViewById(R.id.listview_set);
        wangzhanadapter = new WangzhanAdapter(MainActivity.this,R.layout.settinglistitem,wangzhanList);
        wangzhanInit();
        listView_set.setAdapter(wangzhanadapter);
        listView_set.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        Intent intent1 = new Intent(MainActivity.this,HoneyActivity.class);
                        startActivity(intent1);
                        return false;
                    case 1:
                        return false;
                    case 2:
                        return false;
                    case 3:
                        return false;
                    case 4:
                        return false;
                    case 5:
                        return false;
                    case 6:
                        Toast.makeText(MainActivity.this,"缓存超过300M会显示提示！",Toast.LENGTH_LONG).show();
                        return false;
                    default:
                        return false;
                }

            }
        });
        listView_set.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case 0:
                        //Toast.makeText(MainActivity.this,"兔喔喔 采撷好心情 全网VIP视频免费看",Toast.LENGTH_SHORT).show();
                        Intent intentsm = new Intent(MainActivity.this,ShuomingActivity.class);
                        startActivity(intentsm);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this,GuanyuActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intentg = new Intent(MainActivity.this,GengxinrizhiActivity.class);
                        startActivity(intentg);
                        break;
                    case 3:
                        String[]  strings = {"简单分享APP","高级分享"};
                        new AlertDialog.Builder(MainActivity.this).setItems(strings, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    Intent intent_share = new Intent(Intent.ACTION_SEND);
                                    intent_share.putExtra(Intent.EXTRA_TEXT, "兔喔喔 采撷好心情 全网VIP视频免费看 还有多种小游戏等着你哦  复制下面链接到浏览器 快来下载玩吧\r\n"+gengxinurl);
                                    intent_share.setType("text/plain");
                                    startActivity(Intent.createChooser(intent_share, "分享至"));
                                }
                                else
                                {

                                    String html = "";
                                    html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body bgcolor=\"#ffaa43\"><div class=\"content\"><div class=\"article_con\">";
                                    String p = "";
                                    p+="<p style=\"text-align: center;\"><br><br><br>兔喔喔</p>";
                                    p+="<p style=\"text-align: center;\"><br><br>全网视频免费看<br>纵享VIP影视<br>给你零广告体验<br>还有精选的音乐和资讯<br>以及超凡的游戏等着你哦<br>赶紧下载体验吧<br><br><br></p>";
                                    p+="<p style=\"text-align: center;\"><input type=\"button\" style = \"center\" value=\"下载兔喔喔APP\" onclick=\"javascrtpt:window.location.href='"+gengxinurl+"'\"></p>";
                                    p+="<p style=\"text-align: center;\"<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></p><p></p><p></p><p></p><p></p><p></p>";
                                    html += p;
                                    html+="</div></body></html>";
                                    File file = new File("/sdcard/jingpinwu/用浏览器打开，一起来玩兔喔喔.html");
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
                                        write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                                    } catch (UnsupportedEncodingException e)
                                    {
                                        e.printStackTrace();
                                    } catch (FileNotFoundException e)
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
                                    shareFile(MainActivity.this,file);
                                }
                            }
                        }).create().show();
                        break;
                    case 4:
                        if(isgengxin.contains("yes"))
                        {
                            alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("提示").setMessage("检测到新版本,请下载安装！")
                                    .setPositiveButton("取消", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            alertDialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("更新", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            alertDialog.dismiss();
                                            Uri uri = Uri.parse("http://slark.ys168.com/");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"当前已是最新版本！",Toast.LENGTH_SHORT).show();
                        }
                        /*
                        Intent intent = new Intent();
                        //对应BroadcastReceiver中intentFilter的action
                        intent.setAction("TAB");
                        intent.putExtra("TAB","LIUYAN");
                        //发送广播
                        sendBroadcast(intent);
                        */
                        break;
                    case 5:
                        Intent intent4 = new Intent(MainActivity.this,DSActivity.class);
                        startActivity(intent4);
                        break;
                    case 6:
                        new AlertDialog.Builder(MainActivity.this).setTitle("提示：").setMessage("清除应用数据需要重启兔喔喔，清除后应用将自动退出！").setPositiveButton("清除", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                try
                                {
                                    java.lang.Process process = Runtime.getRuntime().exec("pm clear jingpinwu.android.com");
                                    if(process==null)
                                    {
                                        Toast.makeText(MainActivity.this,"清除失败，请重试!",Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this,"数据清除成功!",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (IOException e)
                                {
                                    Toast.makeText(MainActivity.this,"清除失败，请重试!",Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        }).create().show();
                        break;
                    case 7:
                        Intent intent7 = new Intent(MainActivity.this,FeedbackActivity.class);
                        startActivity(intent7);
                        break;
                    default:
                        break;
                }
            }

        });
        isoversetinit = true;
    }

    @Override
    public void onBackPressed()
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                File[] fileList = file.listFiles();
                if(fileList!=null)
                {
                    for(File f : fileList)
                    {
                        if(!f.isDirectory())
                        {
                            f.delete();
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        MainActivity.this.finish();
    }

    private void wangzhanInit()
    {
        wangzhanList.clear();
        Wangzhan shuoming = new Wangzhan("应用介绍",R.drawable.shuoming);
        wangzhanList.add(shuoming);
        Wangzhan guanyu = new Wangzhan("版本说明",R.drawable.guanyu);
        wangzhanList.add(guanyu);
        Wangzhan gengxinrizhi = new Wangzhan("历史日志",R.drawable.liuyan);
        wangzhanList.add(gengxinrizhi);
        Wangzhan fenxiang = new Wangzhan("好友分享",R.drawable.fenxiang);
        wangzhanList.add(fenxiang);
        Wangzhan cheackgengxin = new Wangzhan("检查更新",R.drawable.gengxin);
        wangzhanList.add(cheackgengxin);
        Wangzhan dashang = new Wangzhan("打赏开发",R.drawable.ds);
        wangzhanList.add(dashang);
        if(totalsize>=300*1024*1024)//大于300M缓存就显示缓存量
        {
            if(totalsize>1024*1024*1024)
            {
                Wangzhan clear = new Wangzhan("清理缓存(" + (int) ((float) (totalsize) / 1024.0 / 1024.0/1024.0*100) /100.00+ "GB)", R.drawable.clear);
                wangzhanList.add(clear);
            }
            else
            {
                Wangzhan clear = new Wangzhan("清理缓存(" + (int) ((float) (totalsize) / 1024.0 / 1024.0*100) /100.00+ "MB)", R.drawable.clear);
                wangzhanList.add(clear);
            }
        }
        else
        {
            Wangzhan clear = new Wangzhan("清除缓存", R.drawable.clear);
            wangzhanList.add(clear);
        }
        Wangzhan fankui = new Wangzhan("问题反馈",R.drawable.fankui);
        wangzhanList.add(fankui);

    }

    void WriteGengxinFile()
    {
        File file = new File("/sdcard/jingpinwufile/gengxin.txt");
        if (file.exists())
        {
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
                writer.write(gengxintxt);
                writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     * @param directory
     */
    private void deleteFilesByDirectory(File directory)
    {
        if (directory != null && directory.exists() && directory.isDirectory())
        {
            for (File item : directory.listFiles())
            {
                item.delete();
            }
        }
    }
    public String queryAppInfo()
    {
        String pkgName="";
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos)
        {
            if(reInfo.activityInfo.packageName.contains("jingpinwu"))
            {
                String activityName = reInfo.activityInfo.name; //获得该应用程序的启动Activity的name
                pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            }
        }
        return pkgName;
    }
    public void  queryPacakgeSize(String pkgName) throws Exception
    {
        if (pkgName != null)
        {
            //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager pm = getPackageManager();  //得到pm对象
            //通过反射机制获得该隐藏函数
            Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,int.class,IPackageStatsObserver.class);
            //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
            getPackageSizeInfo.invoke(pm, pkgName,android.os.Process.myUid() / 100000,new PkgSizeObserver());
        }
    }
    //系统函数，字符串转换 long -String (kb)
    private String formateFileSize(long size)
    {
        return Formatter.formatFileSize(MainActivity.this, size);
    }

    /**
     * ViewPager适配器
     * @author Balla_兔子
     *
     */
    public class ContentAdapter extends PagerAdapter
    {
        public List<View> views;

        public ContentAdapter(List<View> views)
        {
            this.views = views;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = views.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(views.get(position));
        }

        @Override
        public int getCount()
        {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }
    }

    //******************************************************内部类*************************************
    //aidl文件形成的Bindler机制服务类
    public class PkgSizeObserver extends IPackageStatsObserver.Stub
    {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            cachesize = pStats.cacheSize  ; //缓存大小
            datasize = pStats.dataSize  ;  //数据大小
            codesize =	pStats.codeSize  ;  //应用程序大小
            totalsize = cachesize+datasize;//总大小,除了codesize
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getStringExtra("TAB").contains("SEARCH"))
            {
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
            else if(intent.getStringExtra("TAB").contains("LIUYAN"))
            {
                Message message=new Message();
                message.what=3;
                handler.sendMessage(message);
            }
        }
    }
}



