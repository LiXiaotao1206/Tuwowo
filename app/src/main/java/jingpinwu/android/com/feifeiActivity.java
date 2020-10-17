package jingpinwu.android.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class feifeiActivity extends Activity implements EventListener
{

    protected String appId = "11598027";
    protected String appKey = "USFsobPXeZI6fGfSGM8licbP";
    protected String secretKey = "EFPyob372lzhM0sOjqiQmCRHiepWjuDr";
    protected SpeechSynthesizer mSpeechSynthesizer;
    private TextView text_voice;
    private EventManager asr;
    private boolean vinendflg = false;
    private String vtottext = "你好，我叫菲菲";
    private String vinTxt = "";
    private TtsMode ttsMode = TtsMode.ONLINE;
    private boolean dead = false;//判断是否了关闭菲菲标志
    private boolean outover = true;//判断是否小菲说完了
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 100)
            {
                text_voice.setText(vtottext);
            }
            else if(msg.what == 101)
            {
                text_voice.setText("小菲来啦，长按话筒和我聊天吧");
            }
            else if(msg.what == 102)
            {
                text_voice.setText("请退出并检查网络连接！");
            }
            else if(msg.what==10)
            {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.talkbkrelese);
                text_voice.setBackgroundDrawable(new BitmapDrawable(bitmap));
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                //发送停止录音事件，提前结束录音等待识别结果
                asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            }
            else if(msg.what==11)
            {
                if(outover == false)
                {
                    mSpeechSynthesizer.stop();
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.talkbkpress);
                text_voice.setBackgroundDrawable(new BitmapDrawable(bitmap));
                text_voice.setText("");
                outover = false;
                asr.send(SpeechConstant.ASR_START, null, null, 0, 0);
            }
            super.handleMessage(msg);
        }
    };
    Runnable talk_run = new Runnable()
    {
        @Override
        public void run()
        {
            if(vinTxt.equals("拜拜")||vinTxt.equals("退出")||vinTxt.equals("再见"))
            {
                voiceOut("拜拜");
                try
                {
                    Thread.sleep(1500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                feifeiActivity.this.finish();
            }
            else  if(vinTxt.contains("搜索视频")&&(!vinTxt.contains("不要搜索视频"))&&(!vinTxt.contains("别搜索视频"))&&(!vinTxt.contains("不搜索视频")))
            {
                String str = "";
                int a = 0;
                a = vinTxt.indexOf("搜索视频")+4;
                vinTxt = vinTxt.substring(a, vinTxt.length());

                Intent intent = new Intent(feifeiActivity.this, XLListActivity.class);
                intent.putExtra("SEARCHURL", "http://xlyy100.com/index.php?m=vod-search-pg-1-wd-" + vinTxt+".html");
                voiceOut("正在搜索"+vinTxt);
                startActivity(intent);
            }
            else
            {
                talk(vinTxt);
            }
        }
    };
    SpeechSynthesizerListener TtoVlistener = new SpeechSynthesizerListener()
    {
        @Override
        public void onSynthesizeStart(String s)
        {

        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i)
        {

        }

        @Override
        public void onSynthesizeFinish(String s)
        {

        }

        @Override
        public void onSpeechStart(String s)
        {

        }

        @Override
        public void onSpeechProgressChanged(String s, int i)
        {

        }

        @Override
        public void onSpeechFinish(String s)
        {
            Message message = new Message();
            message.what = 10;
            handler.sendMessage(message);
            outover = true;
        }

        @Override
        public void onError(String s, SpeechError speechError)
        {

        }
    };
    Runnable initTtoV_run = new Runnable()
    {
        @Override
        public void run()
        {
            initTTs();
            if(IsNetAvilible.isNetworkAvalible(feifeiActivity.this))
            {
                Message message = new Message();
                message.what = 101;
                handler.sendMessage(message);
            }
            else
            {
                Message message = new Message();
                message.what = 102;
                handler.sendMessage(message);
            }
            while (!dead)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feifei);
        Message message = new Message();
        message.what = 10;
        handler.sendMessage(message);
        initView();
        initPermission();
        initASR();
        new Thread(initTtoV_run).start();//初始化TtoV

        /*
        text_voice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(outover==true)
                {
                    outover = false;
                    Message message = new Message();
                    message.what = 11;
                    handler.sendMessage(message);
                    text_voice.setText("");
                    asr.send(SpeechConstant.ASR_START, null, null, 0, 0);
                }
            }
        });*/
        text_voice.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {

                    case MotionEvent.ACTION_UP:
                        //Log.e("AAAA","up");
                        Message message1 = new Message();
                        message1.what = 10;
                        handler.sendMessage(message1);
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        //Log.e("AAAA","down");
                        Message message2 = new Message();
                        message2.what = 11;
                        handler.sendMessage(message2);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        //Log.e("AAAA","cancel");
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        text_voice.setText("按下话筒和我聊天吧");
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);//释放VtoT
        if (mSpeechSynthesizer != null)//释放TtoV
        {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
        }
        dead = true;
    }

    private void initView()
    {
        text_voice = (TextView) findViewById(R.id.textView_voice);
    }

    protected  void initASR()
    {
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
    }

    private void voiceOut(String outstr)
    {
        if (mSpeechSynthesizer == null)
        {
            return;
        }
        mSpeechSynthesizer.speak(outstr);
    }

    private void stopTtoV()
    {
        mSpeechSynthesizer.stop();
    }

    private void initTTs()
    {
        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        // 2. 设置listener,listener是SpeechSynthesizerListener 的实现类，需要实现自己的业务逻辑。SDK合成后会对这个类的方法进行回调。
        mSpeechSynthesizer.setSpeechSynthesizerListener(TtoVlistener);

        // 3. 设置appId，appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(appId);
        result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "3");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        // 6. 初始化
        result = mSpeechSynthesizer.initTts(ttsMode);
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length)
    {

        if (params != null && !params.isEmpty())
        {
            vinTxt = params;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END))
        {
            vinendflg = true;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL))
        {
            if (params.contains("nlu_result"))
            {
                if (length > 0 && data.length > 0)
                {
                    vinTxt =  new String(data, offset, length);
                    vinTxt = "床前明月光";
                    text_voice.setText(vinTxt);
                    vinendflg = false;
                    new Thread(talk_run).start();
                }
            }
            else if(vinendflg == true)
            {
                /*
                if(vinTxt.contains("best_result"))
                {
                    int a = 0, b = 1;
                    a = vinTxt.indexOf("best_result");
                    b = vinTxt.indexOf("result_type");
                    vinTxt = vinTxt.substring(a+14, b-3);
                    text_voice.append(vinTxt);
                    vinendflg = false;
                    new Thread(talk_run).start();
                }
                */
                if(vinTxt.contains("best_result"))
                {
                    JSONObject jsonObject = null;
                    try
                    {
                        jsonObject = new JSONObject(vinTxt);
                        vinTxt = jsonObject.getString("best_result");
                        vinendflg = false;
                        new Thread(talk_run).start();
                    }
                    catch (JSONException e)
                    {
                        vinTxt = "请检查网络连接！";
                        e.printStackTrace();
                    }
                    text_voice.setText(vinTxt);
                }
            }
        }


    }

    public void talk_tuling(String s)
    {
        String url="http://www.tuling123.com/openapi/api";
        String encode="utf-8";
        final Map<String,String> map=new HashMap<>();
        map.put("key","a105b5f317ba4b7c9ad5b9493bb0e6f8");
        map.put("info",s);
        HttpUtlis.postRequest(url, map,encode, new HttpUtlis.OnResponseListner() {
            @Override
            public void onSucess(String response)
            {
                //if(response.contains("100000"))
                {
                    if (response.contains("text"))
                    {
                        JSONObject jsonObject = null;
                        try
                        {
                            jsonObject = new JSONObject(response);
                            response = jsonObject.getString("text");
                        }
                        catch (JSONException e)
                        {
                            response = "小菲很难过，想静静";
                            e.printStackTrace();
                        }
                        vtottext = response;
                        Message message = new Message();
                        message.what = 100;
                        handler.sendMessage(message);
                    }
                }
               // else
                {
                   // response = "你自己来吧，最近身体不舒服，小菲想休息了";
                   // vtottext = response;
                   // Message message = new Message();
                   // message.what = 100;
                   // handler.sendMessage(message);
                }
                voiceOut(response);
            }

            @Override
            public void onError(String error)
            {
                mSpeechSynthesizer.stop();
            }
        });
    }

    public void talk(String s)//moli
    {
        String urlString="http://i.itpk.cn/api.php?question=";
        String encode="utf-8";
        try
        {
            HttpURLConnection connection = (HttpURLConnection)new URL(urlString+s).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Cache-Control","max-age=0");
            connection.setRequestProperty("Upgrade-Insecure-Requests","1");
            OutputStream os = connection.getOutputStream();
            os.write(s.getBytes());
            os.flush();
            os.close();
            if(connection.getResponseCode()==200)
            {
                InputStream is = connection.getInputStream();
                vtottext = "";
                int len = 0;
                byte[] bytes = new byte[1024];
                while((len = is.read(bytes)) !=-1)
                {
                    vtottext += new String(bytes,0,len);
                }
                is.close();
                if(vtottext.length()>0 && vtottext.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(vtottext);
                    if((vtottext = jsonObject.getString("content"))==null)
                    {
                        vtottext = "小菲有点累了，你也休息一会吧";
                    }
                }
                if(vtottext.contains("[name]"))
                {
                    vtottext = vtottext.replace("[name]","亲亲");
                }
                if(vtottext.contains("[cqname]"))
                {
                    vtottext = vtottext.replace("[cqname]","小菲");
                }
                if(vtottext.contains("茉莉"))
                {
                    vtottext = vtottext.replace("茉莉","小菲");
                }
            }
            else
            {
                vtottext = "小菲有点累了，你也休息一会吧";
            }
        }
        catch(IOException e)
        {
            vtottext = "小菲有点累了，你也休息一会吧";
        }
        catch(JSONException e)
        {
            vtottext = "小菲有点累了，你也休息一会吧";
        }
        voiceOut(vtottext);
        Message message = new Message();
        message.what = 100;
        handler.sendMessage(message);

    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission()
    {
        String permissions[] =
                {
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm :permissions)
        {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm))
            {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty())
        {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }



}
