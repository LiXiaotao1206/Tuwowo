package jingpinwu.android.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




/**
 * Created by lixiaotao on 2018/4/19.
 */

public class PPWView extends SurfaceView implements SurfaceHolder.Callback , SensorEventListener
{
    public Boolean thflg = true;
    private Paint paint;
    private SurfaceHolder sfh;
    private int ScreenW,ScreenH;
    private int guaLocateX = 0,guaLocateY = 0;
    private int guaw,guaH;
    private int guaSpeedX = 10;
    private int guaSpeedY = 10;
    private int waLocateX;
    private int chengji = 0;
    private Bitmap bkbitmap;
    private Bitmap wabitmap;
    private Bitmap wazbitmap;
    private Bitmap warbitmap;
    private Bitmap guabitmap;
    private Bitmap redguabitmap;
    private Bitmap sharebitmap;
    private Bitmap buttonbitmap;
    private Bitmap replaybitmap;
    private int initGuaSpeed = 10;
    private float waSpeed;
    private SensorManager sensorManager;
    private int zhayan = 0;
    private MediaPlayer mediaPlayer_qiang;
    private MediaPlayer mediaPlayer_wajiao;
    private MediaPlayer mediaPlayer_dead;
    Runnable mainrun = new Runnable()
    {
        @Override
        public void run()
        {

            while (thflg)
            {
                long start = System.currentTimeMillis();
                midDraw();
                logic();
                long stop = System.currentTimeMillis();
                try
                {
                    if (stop - start < 10)
                    {
                        Thread.sleep(10 - (stop - start));
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }


            }
        }
    };
    private boolean share = false;
    private boolean goflg = false;
    private int startZhayan = 0;
    Runnable startrun = new Runnable()
    {
        @Override
        public void run()
        {

            while (!goflg)
            {
                startDraw();
                long start = System.currentTimeMillis();
                startZhayan ++;
                if(startZhayan == 1200)
                {
                    startZhayan = 0;
                }
                long stop = System.currentTimeMillis();
                try
                {
                    if (stop - start < 16)
                    {
                        Thread.sleep(16 - (stop - start));
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    public PPWView(Context context)
    {
        super(context);
        chengji = 0;
        mediaPlayer_qiang = MediaPlayer.create(context,R.raw.zhuangqiang);
        mediaPlayer_wajiao = MediaPlayer.create(context,R.raw.wajiao);
        mediaPlayer_dead = MediaPlayer.create(context,R.raw.siwang);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(80);
        sfh = this.getHolder();
        sfh.addCallback(this);
        setFocusable(true);
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        //注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        ScreenW = this.getWidth();
        ScreenH = this.getHeight();
        guaSpeedX = guaSpeedY = ScreenH/200;
        //背景
        Bitmap bitmapB = BitmapFactory.decodeResource(getResources(), R.drawable.ppwbk);
        Matrix matrixB = new Matrix();
        matrixB.postScale((float)ScreenW/bitmapB.getWidth(), (float)ScreenH/bitmapB.getHeight());
        bkbitmap = Bitmap.createBitmap(bitmapB,0,0,bitmapB.getWidth(),bitmapB.getHeight(),matrixB,true);
        //青蛙
        Bitmap bitmapW = BitmapFactory.decodeResource(getResources(), R.drawable.qingwag);
        Matrix matrixW = new Matrix();
        matrixW.postScale((float)ScreenW/bitmapW.getWidth()/4, (float)ScreenH/bitmapW.getHeight()/4/16*9);
        wabitmap = Bitmap.createBitmap(bitmapW,0,0,bitmapW.getWidth(),bitmapW.getHeight(),matrixW,true);
        //眨眼
        Bitmap bitmapWZ = BitmapFactory.decodeResource(getResources(), R.drawable.qingwagb);
        wazbitmap = Bitmap.createBitmap(bitmapWZ,0,0,bitmapWZ.getWidth(),bitmapWZ.getHeight(),matrixW,true);
        //红蛙
        Bitmap bitmapWR = BitmapFactory.decodeResource(getResources(), R.drawable.qingwar);
        warbitmap = Bitmap.createBitmap(bitmapWR,0,0,bitmapWZ.getWidth(),bitmapWZ.getHeight(),matrixW,true);
        //瓜
        Bitmap bitmapG = BitmapFactory.decodeResource(getResources(), R.drawable.xiguagq);
        Matrix matrixG = new Matrix();
        matrixG.postScale((float)ScreenW/bitmapG.getWidth()/10, (float)ScreenH/bitmapG.getHeight()/160*9);
        guabitmap = Bitmap.createBitmap(bitmapG,0,0,bitmapG.getWidth(),bitmapG.getHeight(),matrixG,true);
        //红瓜
        Bitmap bitmapGR = BitmapFactory.decodeResource(getResources(), R.drawable.honggua);
        redguabitmap = Bitmap.createBitmap(bitmapGR,0,0,bitmapG.getWidth(),bitmapG.getHeight(),matrixG,true);

        //分享
        Bitmap bitmapShare = BitmapFactory.decodeResource(getResources(), R.drawable.share);
        Matrix matrixS = new Matrix();
        matrixS.postScale((float)ScreenW/bitmapShare.getWidth(), (float)ScreenH/bitmapShare.getHeight()/16*9);
        sharebitmap = Bitmap.createBitmap(bitmapShare,0,0,bitmapShare.getWidth(),bitmapShare.getHeight(),matrixS,true);

        //分享按钮
        Bitmap bitmapbtn = BitmapFactory.decodeResource(getResources(), R.drawable.textshare);
        Matrix matrixT = new Matrix();
        matrixT.postScale((float)ScreenW/bitmapbtn.getWidth()/5, (float)ScreenH/bitmapbtn.getHeight()/8/16*9);
        buttonbitmap = Bitmap.createBitmap(bitmapbtn,0,0,bitmapbtn.getWidth(),bitmapbtn.getHeight(),matrixT,true);

        //重来
        Bitmap bitmapreplay = BitmapFactory.decodeResource(getResources(), R.drawable.replay);
        Matrix matrixReplay = new Matrix();
        matrixReplay.postScale((float)ScreenW/bitmapreplay.getWidth()/2, (float)ScreenH/bitmapreplay.getWidth()/2/16*9);
        replaybitmap = Bitmap.createBitmap(bitmapreplay,0,0,bitmapreplay.getWidth(),bitmapreplay.getHeight(),matrixReplay,true);

        guaH = guabitmap.getHeight();
        guaw = guabitmap.getWidth();
        goflg = false;
        startDraw();
        new Thread(startrun).start();
    }

    public void Init()
    {
        thflg = true;
        guaLocateX = 0;
        guaLocateY = 0;
        guaSpeedX = 10;
        guaSpeedY = 10;
        chengji = 0;
        initGuaSpeed = 12;
        share = false;
        goflg = false;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        waSpeed = sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    private void startDraw()
    {
        Canvas canvas;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            canvas = sfh.lockHardwareCanvas();
        }
        else
        {
            canvas = sfh.lockCanvas();
        }

        paint.setColor(Color.BLUE);
        canvas.drawRect(0,0,ScreenW,ScreenH,paint);
        if(startZhayan%120>20)
            canvas.drawBitmap(wabitmap, ScreenW/2-wabitmap.getWidth()/2, ScreenH/2 - wabitmap.getHeight()/2, paint);
        else
            canvas.drawBitmap(wazbitmap, ScreenW/2-wabitmap.getWidth()/2, ScreenH/2 - wabitmap.getHeight()/2, paint);
        paint.setColor(Color.YELLOW);
        sfh.unlockCanvasAndPost(canvas);
    }
    public  void midDraw()
    {
        Canvas canvas;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            canvas = sfh.lockHardwareCanvas();
        }
        else
        {
            canvas = sfh.lockCanvas();
        }

        canvas.drawBitmap(bkbitmap, 0, 0, paint);
        if(zhayan%120<20)
        {
            canvas.drawBitmap(wazbitmap, waLocateX, ScreenH - wabitmap.getHeight(), paint);
        }
        else
        {
            canvas.drawBitmap(wabitmap, waLocateX, ScreenH - wabitmap.getHeight(), paint);
        }
        canvas.drawBitmap(guabitmap, guaLocateX, guaLocateY, paint);
        canvas.drawText(""+chengji,ScreenW/2-40,120,paint);
        sfh.unlockCanvasAndPost(canvas);
    }

    public void logic()
    {

        zhayan ++;
        if(zhayan == 1200)
        {
            zhayan = 0;
        }
        if(guaLocateX <= 0)
        {
            guaSpeedX = (initGuaSpeed);
            mediaPlayer_qiang.start();
        }
        if(guaLocateX >= ScreenW-guaw)
        {
            guaSpeedX = (-initGuaSpeed);
            mediaPlayer_qiang.start();
        }
        if(guaLocateY <= 0 )
        {
            guaSpeedY = (initGuaSpeed);
            mediaPlayer_qiang.start();
        }
        if(guaLocateY >= ScreenH - guaH - wabitmap.getHeight())
        {
            if((guaLocateX > waLocateX-guaw)&&(guaLocateX < waLocateX+wabitmap.getWidth()))
            {
                initGuaSpeed += 1;
                chengji++;
                guaSpeedY = (-initGuaSpeed);
                mediaPlayer_wajiao.start();
            }
            else
            {
                guaLocateX -= guaSpeedX;
                sensorManager.unregisterListener(this);
                Canvas canvas = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    canvas = sfh.lockHardwareCanvas();
                }
                else
                {
                    canvas = sfh.lockCanvas();
                }

                canvas.drawBitmap(bkbitmap, 0, 0, paint);
                canvas.drawBitmap(warbitmap, waLocateX, ScreenH-wabitmap.getHeight(), paint);
                canvas.drawBitmap(redguabitmap, guaLocateX, guaLocateY, paint);
                canvas.drawBitmap(sharebitmap, 0, ScreenH/2-sharebitmap.getHeight()/2, paint);
                canvas.drawBitmap(buttonbitmap, ScreenW/2-buttonbitmap.getWidth()/2-10, ScreenH/2+buttonbitmap.getHeight()/4, paint);
                canvas.drawBitmap(replaybitmap, ScreenW/4, ScreenH/4*3+ScreenW/16, paint);
                paint.setColor(Color.YELLOW);
                canvas.drawText(""+chengji,ScreenW/2-50,ScreenH/2-20,paint);
                sfh.unlockCanvasAndPost(canvas);
                mediaPlayer_dead.start();
                thflg = false;
            }

        }
        guaLocateX += guaSpeedX;
        guaLocateY += guaSpeedY;





        if((waLocateX>=0)&&(waLocateX <= ScreenW-wabitmap.getWidth()))
        {
            waLocateX -= ((double)waSpeed*(8.0+initGuaSpeed/3.0));
        }
        if(waLocateX<0)
        {
            waLocateX = 0;
        }
        else if(waLocateX > ScreenW-wabitmap.getWidth())
        {
            waLocateX = ScreenW-wabitmap.getWidth();
        }

    }

    public void setThflg(boolean y)
    {
        if(y == false)
        {
            //关闭时解除加速度传感器  防止费电
            sensorManager.unregisterListener(this);
            mediaPlayer_wajiao.release();
            mediaPlayer_qiang.release();
            mediaPlayer_dead.release();
        }
        thflg = y;
    }
    public void setgoflg(boolean y)
    {
        goflg = y;
    }
    public int getChengji()
    {
        return chengji;
    }
    public boolean getshare()
    {
        return share;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if((thflg == false)&&(goflg == true))
        {
            if((event.getY()>=ScreenH/2+buttonbitmap.getHeight()/4)&&(event.getY()<=ScreenH/2+buttonbitmap.getHeight()/4+buttonbitmap.getHeight())&&(event.getX()>=ScreenW/2-buttonbitmap.getWidth()/2)&&(event.getX()<=ScreenW/2-buttonbitmap.getWidth()/2+buttonbitmap.getWidth()))
            {
                share = true;
            }
            if((event.getY()>ScreenH/4*3+ScreenW/16)&&(event.getX()>ScreenW/4)&&(event.getX()<ScreenW/4*3))
            {
                Init();
                new Thread(startrun).start();
            }
        }
        if(goflg == false)
        {
            goflg = true;
            new Thread(mainrun).start();
        }
        return super.onTouchEvent(event);
    }
}
