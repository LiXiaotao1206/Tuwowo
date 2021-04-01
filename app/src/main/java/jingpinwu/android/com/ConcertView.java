package jingpinwu.android.com;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;
import java.util.Random;


/**
 * Created by lixiaotao on 2018/4/19.
 */

public class ConcertView extends SurfaceView implements SurfaceHolder.Callback
{
    private Paint paint;
    private SurfaceHolder sfh;
    private int ScreenW,ScreenH;
    private boolean thflg = false;
    private String text = "欢迎来到兔喔喔  ";
    private int textSize;
    private int textLength;
    private int textLocate;
    private int textLocate2;
    private int color;
    Runnable mainrun = new Runnable()
    {
        @Override
        public void run()
        {
            while (thflg)
            {
                long start = System.currentTimeMillis();
                Logic();
                midDraw();
                long stop = System.currentTimeMillis();
                try
                {
                    if (stop - start < 20)
                    {
                        Thread.sleep(20 - (stop - start));
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }
    };

    public ConcertView(Context context,String text,int size,int color)
    {
        super(context);
        paint = new Paint();
        sfh = this.getHolder();
        sfh.addCallback(this);
        setFocusable(true);
        this.text = text;
        this.textSize = size;
        this.color = color;
        //myDraw(count,true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {

        ScreenW = this.getWidth();
        ScreenH = this.getHeight();
        textSize = (4-textSize)/4*ScreenW/3;
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textLength = (int)textPaint.measureText(text);

        textLocate = (ScreenW - textLength) / 2;
        textLocate2 = textLocate + textLength;

        midDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //TouchY = (int)event.getY();

        return false;
    }
    public  void midDraw()
    {

        Canvas canvas = sfh.lockCanvas();
        paint.setColor(Color.rgb(0,0,0));
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
        paint.setTextSize(textSize);
        paint.setColor(color);

        canvas.drawText(text,textLocate,ScreenH/2+textSize*3/4/2,paint);
        if(textLength>ScreenW)
        {
            canvas.drawText(text, textLocate2, ScreenH / 2 + textSize * 3 / 4 / 2, paint);
        }
        sfh.unlockCanvasAndPost(canvas);
    }

    private void Logic()
    {

        if(textLength > ScreenW)
        {
            if(textLocate > 0 && textLocate <= ScreenW/180)
            {
                textLocate2 = textLocate + textLength;

            }
            if(textLocate2 > 0 && textLocate2  <= ScreenW/180)
            {
                textLocate = textLocate2 + textLength;
            }
        }
        else
        {
            if(textLocate > ScreenW)
            {
                textLocate = -textLength;
                textLocate2 = 0;
            }
        }
        textLocate -= ScreenW/180;
        textLocate2 -= ScreenW/180;
    }
    public void setColor(int color)
    {
        this.color = color;
    }
    public void setSize(int size)
    {
        this.textSize = (int)(((double)(4-size))/4*ScreenW/2);
    }
    public void setText(String string)
    {
        this.text = string;
    }
    public void setGo(boolean f)
    {
        this.thflg = f;
    }
    public void setLength()
    {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textLength = (int)textPaint.measureText(text);
        if(ScreenW>textLength)
        {
            textLocate = (ScreenW - textLength) / 2;
            midDraw();
        }
        else
        {
            new Thread(mainrun).start();
        }
    }
}
