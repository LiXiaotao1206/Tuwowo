package jingpinwu.android.com;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    //子线程不能操作UI，通过Handler设置图片
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;
            }
        }
    };

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMyImageBitmap(Bitmap bitmap)
    {
        Message msg = new Message();
        msg.obj = bitmap;
        msg.what = GET_DATA_SUCCESS;
        handler.sendMessage(msg);
    }
    //设置网络图片
    public void setImageURL(final String path) {

        //开启一个线程用于联网
        new Thread() {
            @Override
            public void run() {
                try
                {
                    if(path==null)
                    {
                        return;
                    }
                    if(!path.startsWith("http"))
                    {
                        return;
                    }
                    //把传过来的路径转成URL
                    URL url = new URL(path);
                    int code = 0;
                    InputStream inputStream = null;
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(100000);
                    code = connection.getResponseCode();
                    inputStream = connection.getInputStream();

                    if (code == 200 && inputStream!=null)
                    {
                        //使用工厂把网络的输入流生产Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //利用Message把图片发给Handler
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        inputStream.close();
                    }
                    else
                    {
                        if(!path.contains("http"))
                        {
                            return;
                        }
                        String httpsstr = "https" + path.substring(4,path.length());
                        HttpsURLConnection connections = (HttpsURLConnection)new URL(httpsstr).openConnection();
                        connections.setRequestMethod("GET");
                        connections.setConnectTimeout(100000);
                        inputStream = connections.getInputStream();

                        if(inputStream!=null)
                        {

                            //使用工厂把网络的输入流生产Bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            //利用Message把图片发给Handler
                            Message msg = Message.obtain();
                            msg.obj = bitmap;
                            msg.what = GET_DATA_SUCCESS;
                            handler.sendMessage(msg);
                            inputStream.close();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    //网络连接错误
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();
    }

}

