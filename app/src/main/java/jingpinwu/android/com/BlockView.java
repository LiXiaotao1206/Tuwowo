package jingpinwu.android.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.text.DecimalFormat;
import java.util.Random;


/**
 * Created by lixiaotao on 2018/4/19.
 */

public class BlockView extends SurfaceView implements SurfaceHolder.Callback
{
    public Boolean thflg = false;
    private Paint paint;
    private SurfaceHolder sfh;
    private int TouchX = 10;
    private int TouchY = 10;
    private Thread mainthread;
    private int ScreenW,ScreenH;
    private int[] count = new int[4];
    private int[] color = new int[4];
    private double time = 0.00;
    private int chengji = 0;
    private boolean cango = false;
    private Boolean exit = false;
    private Boolean manfen = false;
    private Boolean cangetmanfen = false;
    private Bitmap bkbitmap;
    Runnable mainrun = new Runnable()
    {
        @Override
        public void run()
        {
            time = 0.00;
            while (thflg)
            {
                long start = System.currentTimeMillis();
                midDraw(count, true);
                long stop = System.currentTimeMillis();
                try
                {
                    if (stop - start < 50)
                    {
                        Thread.sleep(50 - (stop - start));
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                time = time + 50.00 / 1000.00;

            }
        }
    };

    public BlockView(Context context)
    {
        super(context);
        exit = false;
        cango = true;
        time = 0.00;
        chengji = 0;
        manfen = false;
        cangetmanfen = false;
        paint = new Paint();
        sfh = this.getHolder();
        sfh.addCallback(this);
        setFocusable(true);
        count[0] = 0;
        count[1] = new Random().nextInt(4)+1;
        count[2] = new Random().nextInt(4)+1;
        count[3] = new Random().nextInt(4)+1;
        color[0] = Color.WHITE;
        color[1] = Color.GREEN;
        color[2] = Color.RED;
        color[3] = Color.YELLOW;



        //myDraw(count,true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        exit = false;
        cango = true;
        time = 0.00;
        chengji = 0;
        manfen = false;
        cangetmanfen = false;
        ScreenW = this.getWidth();
        ScreenH = this.getHeight();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.musicplaybk);
        Matrix matrix = new Matrix();
        matrix.postScale((float)ScreenW/bitmap.getWidth(), (float)ScreenH/bitmap.getHeight());
        bkbitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        midDraw(count,true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        TouchY = (int)event.getY();
        if(cango == true)
        {
            if (TouchY > ScreenH / 2 && TouchY < ScreenH / 4 * 3)
            {
                TouchX = (int) event.getX();
                logic();
            }

        }
        else
        {
            if (TouchY < ScreenH/10+ScreenH / 7*6 && TouchY > (ScreenH / 7 * 6-ScreenH/20))
            {
                TouchX = (int) event.getX();
                if(TouchX>ScreenW/2-ScreenH/20 && TouchX<ScreenW/2+ScreenH/10)  //重新开始
                {
                    count[0] = 0;
                    count[1] = new Random().nextInt(4)+1;
                    count[2] = new Random().nextInt(4)+1;
                    count[3] = new Random().nextInt(4)+1;
                    cango = true;
                    time = 0;
                    chengji = 0;
                    cangetmanfen = false;
                    midDraw(count,true);
                }
            }
            if (TouchY < ScreenH/ 7*5  && TouchY > (ScreenH /7 *5-ScreenH/10))
            {
                TouchX = (int) event.getX();
                if(TouchX>ScreenW/2-ScreenH/20 && TouchX<ScreenW/2+ScreenH/10)  //分享
                {
                    if(manfen == true)
                    {
                        cangetmanfen = true;
                    }
                }
            }
        }
        return false;
    }
    public boolean getExit()
    {
        return exit;
    }
    public boolean getcanManfen()
    {
        return cangetmanfen;
    }
    public void setcanManfen(boolean y)
    {
        cangetmanfen = y;
    }
    public double getTime()
    {
        return time;
    }
    public  void midDraw(int[] c,boolean y)
    {
        Canvas canvas = sfh.lockCanvas();
        if(y == true)
        {
            //paint.setColor(Color.BLUE);
            canvas.drawBitmap(bkbitmap, 0, 0, paint);
            //canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
            paint.setColor(Color.GRAY);
            if (c[0] < 5)
                canvas.drawRect(ScreenW / 4 * (c[0] - 1), ScreenH / 4 * 3, ScreenW / 4 * c[0], ScreenH, paint);

            if (c[1] < 5)
            {
                paint.setColor(color[1]);
                canvas.drawRect(ScreenW / 4 * (c[1] - 1), ScreenH / 4 * 2, ScreenW / 4 * c[1], ScreenH / 4 * 3, paint);
            }
                if (c[2] < 5)
            {
                paint.setColor(color[2]);
                canvas.drawRect(ScreenW / 4 * (c[2] - 1), ScreenH / 4, ScreenW / 4 * c[2], ScreenH / 2, paint);
            }
            if (c[3] < 5)
            {
                paint.setColor(color[3]);
                canvas.drawRect(ScreenW / 4 * (c[3] - 1), 0, ScreenW / 4 * c[3], ScreenH / 4, paint);
            }
            /*
            paint.setColor(Color.BLUE);
            canvas.drawLine(ScreenW / 4, 0, ScreenW / 4, ScreenH, paint);
            canvas.drawLine(ScreenW / 2, 0, ScreenW / 2, ScreenH, paint);
            canvas.drawLine(ScreenW / 4 * 3, 0, ScreenW / 4 * 3, ScreenH, paint);
            canvas.drawLine(0, ScreenH / 4, ScreenW, ScreenH / 4, paint);
            canvas.drawLine(0, ScreenH / 2, ScreenW, ScreenH / 2, paint);
            canvas.drawLine(0, ScreenH / 4 * 3, ScreenW, ScreenH / 4 * 3, paint);
            */
            paint.setTextSize(ScreenH/30);
            paint.setColor(Color.WHITE);
            if(time>999.000)
            {
                midDraw(count,false);
            }
            DecimalFormat df = new DecimalFormat("000.000");
            canvas.drawText(String.valueOf(df.format(time)),ScreenW/2-ScreenW/10,ScreenH/10,paint);
            canvas.drawText(String.valueOf(chengji),ScreenW/2-ScreenW/30,ScreenH/10-ScreenH/23,paint);
            if(chengji >= 98)
            {
                paint.setColor(Color.GREEN);
                canvas.drawRect(0, 0, ScreenW, ScreenH / 4, paint);
                paint.setColor(Color.RED);
                paint.setTextSize(ScreenH/30);
                canvas.drawText(String.valueOf(df.format(time)),ScreenW/2-ScreenW/10,ScreenH/7,paint);
                canvas.drawText(String.valueOf(chengji),ScreenW/2-ScreenW/30,ScreenH/7-ScreenH/23,paint);
            }
            if(chengji >= 99)
            {
                paint.setColor(Color.GREEN);
                canvas.drawRect(0, 0, ScreenW, ScreenH / 4, paint);
                canvas.drawRect(0,ScreenW/4,ScreenW, ScreenH / 2, paint);
                paint.setColor(Color.RED);
                paint.setTextSize(ScreenH/20);
                canvas.drawText(String.valueOf(df.format(time)),ScreenW/2-ScreenW/7,ScreenH/4+ScreenH/40,paint);
                canvas.drawText(String.valueOf(chengji),ScreenW/2-ScreenW/20,ScreenH/4-ScreenH/20,paint);
            }
            if(chengji == 100)
            {
                paint.setColor(Color.GREEN);
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
                paint.setColor(Color.RED);
                paint.setTextSize(ScreenH/11);
                canvas.drawText(""+df.format(time),ScreenW/2-ScreenH/6,ScreenH/2,paint);
                paint.setTextSize(ScreenH/30);
                canvas.drawText("继续挑战",ScreenW/2-ScreenH/20,ScreenH/7*6,paint);
                canvas.drawText("分享战绩",ScreenW/2-ScreenH/20,ScreenH/7*5,paint);
                thflg = false;
                cango = false;
                manfen = true;
            }
        }
        else
        {
            thflg = false;
            cango = false;
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, ScreenW, ScreenH, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(ScreenH/10);
            canvas.drawText("失败",ScreenW/2-ScreenH/10,ScreenH/2,paint);
            paint.setColor(Color.GREEN);
            paint.setTextSize(ScreenH/30);
            canvas.drawText("继续挑战",ScreenW/2-ScreenH/20,ScreenH/7*6,paint);
        }
        sfh.unlockCanvasAndPost(canvas);
    }

    public void logic()
    {
        if((count[1]-1) == (int)(TouchX/(ScreenW/4)))//点击正确
        {
            thflg = true;
            if (chengji == 0)
            {
                mainthread = new Thread(mainrun);
                mainthread.start();
            }
            chengji ++;
            count[0] = count[1];
            count[1] = count[2];
            count[2] = count[3];
            count[3] = new Random().nextInt(4)+1;
            color[0] = color[1];
            color[1] = color[2];
            color[2] = color[3];
            color[3] = color[0];
            midDraw(count,true);
        }
        else//错误
        {
            midDraw(count,false);
        }
    }
    public void setThflg(boolean y)
    {
        this.thflg = y;
    }
}
