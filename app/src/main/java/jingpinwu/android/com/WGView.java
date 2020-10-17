package jingpinwu.android.com;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;


/**
 * Created by lixiaotao on 2018/4/19.
 */

public class WGView extends SurfaceView implements SurfaceHolder.Callback
{
    public Boolean thflg = true;
    int a=0;
    private String gengxinurl;
    private Paint paint;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private int ScreenW,ScreenH;
    private Bitmap bkbitmap1;
    private Bitmap bkbitmap2;
    private Bitmap wuguibitmap1;
    private Bitmap wuguibitmap2;
    private Bitmap wuguibitmapfu1;
    private Bitmap wuguibitmapfu2;
    private Bitmap hualeft;
    private Bitmap huaright;
    private Bitmap sharebitmap;
    private Bitmap buttonbitmap;
    private Bitmap gobitmap;
    private Bitmap bitmaphua1;
    private Bitmap bitmaphua2;
    private Bitmap bitmaphua3;
    private Bitmap bitmaphua4;
    private Bitmap bitmaphua5;
    private Bitmap bitmaphua6;
    private Bitmap bitmaphua7;
    private Bitmap bitmapleft;
    private Bitmap bitmapright;
    private Bitmap replaybitmap;
    private MediaPlayer mediaPlayer_dead;
    private boolean goflg = true;
    private boolean share;
    private int chengji;
    private int[] huaflgleft = new int[4];
    private int[] huaflgright = new int[4];
    private int[] hualocateH = new int[4];
    private int[] huaclassleft = new int[9];
    private int[] huaclassright = new int[9];
    private int bklocate1 = 0;
    private int bklocate2 = 0;
    private int bujin;
    private int guileftlr = 1;
    private int guirightlr = 0;
    private int guipa = 0;
    private float x1=0,x2=0,y1=0,y2=0;
    private int mActivePointerId = 999;
    private int firstlorr = 0;
    private int speed = 150;
    private double julifenzi = 130.0;
    private double julifenmu = 160.0;
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
                    if (stop - start < 16)
                    {
                        Thread.sleep(16 - (stop - start));
                    }
                } catch (InterruptedException e)
                {

                }
            }
        }
    };

    public WGView(Context context)
    {
        super(context);
        gengxinurl = "http://slark.ys168.com/";//https://app.oatos.com/os/share.html?lang=zh_cn&lc=uz98fy";
        mediaPlayer_dead = MediaPlayer.create(context,R.raw.siwang);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(80);
        sfh = this.getHolder();
        sfh.addCallback(this);
        setFocusable(true);
    }

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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        ScreenW = this.getWidth();
        ScreenH = this.getHeight();
        bujin = ScreenH / speed;
        bklocate2 = -ScreenH ;
        for(int i=0;i<4;i++)
        {
            hualocateH[i] = (int)(-ScreenW/4-(double)ScreenW*julifenzi/julifenmu*i);
            huaflgleft[i] = new Random().nextInt(2);
            huaflgright[i] = new Random().nextInt(2);
            huaclassleft[i] = new Random().nextInt(9);
            huaclassright[i] = new Random().nextInt(9);
        }
        //背景
        Bitmap bitmapB = BitmapFactory.decodeResource(getResources(), R.drawable.wgbk);
        Matrix matrixB = new Matrix();
        matrixB.postScale((float)ScreenW/bitmapB.getWidth(), (float)ScreenH/bitmapB.getHeight());
        bkbitmap1 = Bitmap.createBitmap(bitmapB,0,0,bitmapB.getWidth(),bitmapB.getHeight(),matrixB,true);
        bkbitmap2 = Bitmap.createBitmap(bitmapB,0,0,bitmapB.getWidth(),bitmapB.getHeight(),matrixB,true);
        //乌龟1
        Bitmap bitmapW = BitmapFactory.decodeResource(getResources(), R.drawable.gui1);
        Matrix matrixW = new Matrix();
        matrixW.postScale((float)ScreenW/bitmapW.getWidth()/4, (float)ScreenH/bitmapW.getHeight()/4/16*9);
        wuguibitmap1 = Bitmap.createBitmap(bitmapW,0,0,bitmapW.getWidth(),bitmapW.getHeight(),matrixW,true);
        //乌龟2
        Bitmap bitmapW2 = BitmapFactory.decodeResource(getResources(), R.drawable.gui2);
        wuguibitmap2 = Bitmap.createBitmap(bitmapW2,0,0,bitmapW.getWidth(),bitmapW.getHeight(),matrixW,true);
       //乌龟副1
        Bitmap bitmapWf = BitmapFactory.decodeResource(getResources(), R.drawable.guiw1);
        wuguibitmapfu1 = Bitmap.createBitmap(bitmapWf,0,0,bitmapW.getWidth(),bitmapW.getHeight(),matrixW,true);
        //乌龟副2
        Bitmap bitmapWf2 = BitmapFactory.decodeResource(getResources(), R.drawable.guiw2);
        wuguibitmapfu2 = Bitmap.createBitmap(bitmapWf2,0,0,bitmapW.getWidth(),bitmapW.getHeight(),matrixW,true);
        //开始按钮
        Bitmap bitmapGo = BitmapFactory.decodeResource(getResources(), R.drawable.wggo);
        Matrix matrixG = new Matrix();
        matrixG.postScale((float)ScreenW/bitmapGo.getWidth(), (float)ScreenH/bitmapGo.getHeight());
        gobitmap = Bitmap.createBitmap(bitmapGo,0,0,bitmapGo.getWidth(),bitmapGo.getHeight(),matrixG,true);
        //左按钮
        Bitmap bitmapbtnleft = BitmapFactory.decodeResource(getResources(), R.drawable.btn30f);
        Matrix matrixleft = new Matrix();
        matrixleft.postScale((float)ScreenW/bitmapbtnleft.getWidth()/4*3/4, (float)ScreenH/bitmapbtnleft.getHeight()/4/16*9*3/4);
        bitmapleft = Bitmap.createBitmap(bitmapbtnleft,0,0,bitmapbtnleft.getWidth(),bitmapbtnleft.getHeight(),matrixleft,true);
        //右按钮
        Bitmap bitmapbtnright = BitmapFactory.decodeResource(getResources(), R.drawable.btn30r);
        Matrix matrixright = new Matrix();
        matrixright.postScale((float)ScreenW/bitmapbtnright.getWidth()/4*3/4, (float)ScreenH/bitmapbtnright.getHeight()/4/16*9*3/4);
        bitmapright = Bitmap.createBitmap(bitmapbtnright,0,0,bitmapbtnright.getWidth(),bitmapbtnright.getHeight(),matrixright,true);

        //左边花
        Bitmap bitmapHf = BitmapFactory.decodeResource(getResources(), R.drawable.hualeft);
        Matrix matrixHf = new Matrix();
        matrixHf.postScale((float)ScreenW/bitmapHf.getWidth()/4, (float)ScreenH/bitmapHf.getHeight()/4/16*9);
        hualeft = Bitmap.createBitmap(bitmapHf,0,0,bitmapHf.getWidth(),bitmapHf.getHeight(),matrixHf,true);
        //右边花
        Bitmap bitmapHr = BitmapFactory.decodeResource(getResources(), R.drawable.huaright);
        Matrix matrixHr = new Matrix();
        matrixHr.postScale((float)ScreenW/bitmapHr.getWidth()/4, (float)ScreenH/bitmapHr.getHeight()/4/16*9);
        huaright = Bitmap.createBitmap(bitmapHr,0,0,bitmapHr.getWidth(),bitmapHr.getHeight(),matrixHr,true);
        //花1-7
        Bitmap bitmapHua1 = BitmapFactory.decodeResource(getResources(), R.drawable.hua1);
        Matrix matrixHua = new Matrix();
        matrixHua.postScale((float)ScreenW/bitmapHua1.getWidth()/4, (float)ScreenH/bitmapHua1.getHeight()/4/16*9);
        bitmaphua1 = Bitmap.createBitmap(bitmapHua1,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua2 = BitmapFactory.decodeResource(getResources(), R.drawable.hua2);
        bitmaphua2 = Bitmap.createBitmap(bitmapHua2,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua3 = BitmapFactory.decodeResource(getResources(), R.drawable.hua3);
        bitmaphua3 = Bitmap.createBitmap(bitmapHua3,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua4 = BitmapFactory.decodeResource(getResources(), R.drawable.hua4);
        bitmaphua4 = Bitmap.createBitmap(bitmapHua4,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua5 = BitmapFactory.decodeResource(getResources(), R.drawable.hua5);
        bitmaphua5 = Bitmap.createBitmap(bitmapHua5,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua6 = BitmapFactory.decodeResource(getResources(), R.drawable.hua6);
        bitmaphua6 = Bitmap.createBitmap(bitmapHua6,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        Bitmap bitmapHua7 = BitmapFactory.decodeResource(getResources(), R.drawable.hua7);
        bitmaphua7 = Bitmap.createBitmap(bitmapHua7,0,0,bitmapHua1.getWidth(),bitmapHua1.getHeight(),matrixHua,true);

        //分享
        Bitmap bitmapShare = BitmapFactory.decodeResource(getResources(), R.drawable.share);
        Matrix matrixS = new Matrix();
        matrixS.postScale((float)ScreenW/bitmapShare.getWidth(), (float)ScreenH/bitmapShare.getHeight()/16*9);
        sharebitmap = Bitmap.createBitmap(bitmapShare,0,0,bitmapShare.getWidth(),bitmapShare.getHeight(),matrixS,true);

        //重来
        Bitmap bitmapreplay = BitmapFactory.decodeResource(getResources(), R.drawable.replay);
        Matrix matrixReplay = new Matrix();
        matrixReplay.postScale((float)ScreenW/bitmapreplay.getWidth()/2, (float)ScreenH/bitmapreplay.getWidth()/2/16*9);
        replaybitmap = Bitmap.createBitmap(bitmapreplay,0,0,bitmapreplay.getWidth(),bitmapreplay.getHeight(),matrixReplay,true);

        //分享按钮
        Bitmap bitmapbtn = BitmapFactory.decodeResource(getResources(), R.drawable.textshare);
        Matrix matrixT = new Matrix();
        matrixT.postScale((float)ScreenW/bitmapbtn.getWidth()/5, (float)ScreenH/bitmapbtn.getHeight()/8/16*9);
        buttonbitmap = Bitmap.createBitmap(bitmapbtn,0,0,bitmapbtn.getWidth(),bitmapbtn.getHeight(),matrixT,true);
        goflg = false;
        startDraw();
        //new Thread(mainrun).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    public void Init()
    {
        this.setClickable(true);
        thflg = true;
        goflg = false;
        bklocate1 = 0;
        bklocate2 = 0;
        guileftlr = 1;
        guirightlr = 0;
        guipa = 0;
        x1=0;x2=0;y1=0;y2=0;
        firstlorr = 0;
        speed = 150;
        julifenzi = 120.0;
        julifenmu = 160.0;
        chengji = 0;
        share = false;
        bujin = ScreenH / speed;
        bklocate2 = -ScreenH ;
        for(int i=0;i<4;i++)
        {
            hualocateH[i] = (int)(-ScreenW/4-(double)ScreenW*julifenzi/julifenmu*i);
            huaflgleft[i] = new Random().nextInt(2);
            huaflgright[i] = new Random().nextInt(2);
            huaclassleft[i] = new Random().nextInt(9);
            huaclassright[i] = new Random().nextInt(9);
        }
    }

    private void startDraw()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            canvas = sfh.lockHardwareCanvas();
        }
        else
        {
            canvas = sfh.lockCanvas();
        }
        canvas.drawBitmap(gobitmap, 0, 0, paint);
        sfh.unlockCanvasAndPost(canvas);
    }

    public  void midDraw()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            canvas = sfh.lockHardwareCanvas();
        }
        else
        {
            canvas = sfh.lockCanvas();
        }

        canvas.drawBitmap(bkbitmap1, 0, bklocate1, paint);
        canvas.drawBitmap(bkbitmap2, 0, bklocate2, paint);

        if(guipa>10)
        {
            canvas.drawBitmap(wuguibitmap1, guileftlr*ScreenW / 4, ScreenH / 2+ScreenW/4, paint);
            canvas.drawBitmap(wuguibitmapfu2, guirightlr*ScreenW / 4+ScreenW / 2, ScreenH / 2+ScreenW/4, paint);
        }
        else
        {
            canvas.drawBitmap(wuguibitmap2, guileftlr*ScreenW / 4, ScreenH / 2+ScreenW/4, paint);
            canvas.drawBitmap(wuguibitmapfu1, guirightlr*ScreenW / 4+ScreenW / 2, ScreenH / 2+ScreenW/4, paint);
        }
        for(int i = 0;i<4;i++)
        {
            switch (huaclassleft[i])
            {
                case 0:
                    canvas.drawBitmap(hualeft, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 1:
                    canvas.drawBitmap(huaright, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 2:
                    canvas.drawBitmap(bitmaphua1, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 3:
                    canvas.drawBitmap(bitmaphua2, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 4:
                    canvas.drawBitmap(bitmaphua3, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 5:
                    canvas.drawBitmap(bitmaphua4, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 6:
                    canvas.drawBitmap(bitmaphua5, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                case 7:
                    canvas.drawBitmap(bitmaphua6, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
                default:
                    canvas.drawBitmap(bitmaphua7, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    break;
            }
            switch (huaclassright[i])
            {
                case 0:
                    canvas.drawBitmap(hualeft, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 1:
                    canvas.drawBitmap(huaright, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 2:
                    canvas.drawBitmap(bitmaphua1, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 3:
                    canvas.drawBitmap(bitmaphua2, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 4:
                    canvas.drawBitmap(bitmaphua3, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 5:
                    canvas.drawBitmap(bitmaphua4, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 6:
                    canvas.drawBitmap(bitmaphua5, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                case 7:
                    canvas.drawBitmap(bitmaphua6, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
                default:
                    canvas.drawBitmap(bitmaphua7, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    break;
            }
        }
        canvas.drawBitmap(bitmapleft, ScreenW*2/16, ScreenH-ScreenW/16*7, paint);
        canvas.drawBitmap(bitmapright, ScreenW*11/16, ScreenH-ScreenW/16*7, paint);
        canvas.drawText(""+chengji,ScreenW/2-40,120,paint);
        sfh.unlockCanvasAndPost(canvas);
    }

    public void logic()
    {
        guipa++;
        if(guipa>20)
        {
            guipa = 0;
        }
        bklocate1+=bujin;
        bklocate2+=bujin;

        for(int i=0;i<4;i++)
        {
            hualocateH[i]+=bujin;
        }
        if(bklocate1 > 0 && bklocate1 <= bujin)
        {
            bklocate2 = bklocate1 - ScreenH;

        }
        if(bklocate2 > 0 && bklocate2  <= bujin)
        {
            bklocate1 = bklocate2 - ScreenH;
        }
        for(int i=0;i<4;i++)
        {
            if(hualocateH[i]>ScreenH)
            {
                huaflgleft[i] = new Random().nextInt(2);
                huaflgright[i] = new Random().nextInt(2);
                huaclassleft[i] = new Random().nextInt(9);
                huaclassright[i] = new Random().nextInt(9);
                if(i!=0)
                {
                    hualocateH[i] = (int)(hualocateH[i-1]-ScreenW*julifenzi/julifenmu);
                }
                else
                {
                    hualocateH[0] = (int)(hualocateH[3]-ScreenW*julifenzi/julifenmu);
                }
                chengji++;
                if(julifenzi>=110)
                {
                    julifenzi--;
                }
                if(speed>=80)
                {
                    speed--;
                    bujin = ScreenH / speed;
                }
            }
            else if((hualocateH[i] <= ScreenH / 2+ScreenW/2-ScreenW/32)&&(hualocateH[i] > ScreenH /2+ScreenW/32))
            {
                if((guileftlr == huaflgleft[i])||(guirightlr == huaflgright[i]))
                {
                    thflg = false;
                    Canvas canvas;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        canvas = sfh.lockHardwareCanvas();
                    }
                    else
                    {
                        canvas = sfh.lockCanvas();
                    }

                    canvas.drawBitmap(bkbitmap1, 0, bklocate1, paint);
                    canvas.drawBitmap(bkbitmap2, 0, bklocate2, paint);
                    canvas.drawBitmap(wuguibitmap1, guileftlr*ScreenW / 4, ScreenH / 2+ScreenW/4, paint);
                    canvas.drawBitmap(wuguibitmap2, guirightlr*ScreenW / 4+ScreenW / 2, ScreenH / 2+ScreenW/4, paint);
                    canvas.drawBitmap(hualeft, huaflgleft[i]*ScreenW/4, hualocateH[i], paint);
                    canvas.drawBitmap(huaright, huaflgright[i]*ScreenW/4+ScreenW/2, hualocateH[i], paint);
                    canvas.drawBitmap(sharebitmap, 0, ScreenH/2-sharebitmap.getHeight()/2, paint);
                    canvas.drawBitmap(buttonbitmap, ScreenW/2-buttonbitmap.getWidth()/2-10, ScreenH/2+buttonbitmap.getHeight()/4, paint);
                    canvas.drawBitmap(replaybitmap, ScreenW/4, ScreenH/4*3+ScreenW/16, paint);
                    paint.setColor(Color.YELLOW);
                    canvas.drawText(""+chengji,ScreenW/2-50,ScreenH/2-20,paint);
                    sfh.unlockCanvasAndPost(canvas);
                    mediaPlayer_dead.start();
                }
            }
        }

    }

    public void setThflg(boolean y)
    {
        if(y == false)
        {
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.setClickable(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        final int action = MotionEventCompat.getActionMasked(event);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                x1 = MotionEventCompat.getX(event, pointerIndex);
                y1 = MotionEventCompat.getY(event, pointerIndex);
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                if ((thflg == false) && (goflg == true))
                {
                    if((y1>=ScreenH/2+buttonbitmap.getHeight()/4)&&(y1<=ScreenH/2+buttonbitmap.getHeight()/4+buttonbitmap.getHeight())&&(x1>=ScreenW/2-buttonbitmap.getWidth()/2)&&(x1<=ScreenW/2-buttonbitmap.getWidth()/2+buttonbitmap.getWidth()))
                    {
                        //this.setClickable(false);
                        //share = true;
                        shareClicked();
                    }
                    if((event.getY()>ScreenH/4*3+ScreenW/16)&&(event.getX()>ScreenW/4)&&(event.getX()<ScreenW/4*3))
                    {
                        Init();
                        startDraw();
                    }
                }
                if ((thflg == true) && (goflg == true))
                {
                    if ((y1 >= ScreenH - ScreenW / 2) && (x1 <= ScreenW / 2))
                    {
                        guileftlr = 0;
                        firstlorr = 0;
                    }
                    else if ((y1 >= ScreenH - ScreenW / 2) && (x1 > ScreenW / 2))
                    {
                        guirightlr = 1;
                        firstlorr = 1;
                    }
                }
                if(goflg == false)
                {
                    if((y1>=ScreenH/4)&&(y1<=ScreenH/2))
                    {
                        goflg = true;
                        new Thread(mainrun).start();
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                //final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                //if (pointerId != mActivePointerId)
                {
                    x2 = MotionEventCompat.getX(event, pointerIndex);
                    y2 = MotionEventCompat.getY(event, pointerIndex);
                    if ((thflg == true) && (goflg == true))
                    {
                        if ((y2 >= ScreenH - ScreenW / 2) && (x2 <= ScreenW / 2))
                        {
                            guileftlr = 0;
                        } else if ((y2 >= ScreenH - ScreenW / 2) && (x2 > ScreenW / 2))
                        {
                            guirightlr = 1;
                        }
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                if (pointerId == mActivePointerId)
                {
                    if (firstlorr == 0)
                    {
                        guileftlr = 1;
                    }
                    else
                    {
                        guirightlr = 0;
                    }

                }
                else
                {
                    if (firstlorr == 0)
                    {
                        guirightlr = 0;
                    }
                    else
                    {
                        guileftlr = 1;
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                guileftlr = 1;
                guirightlr = 0;
            }
            return true;
            default:
                return false;

        }

    }

    public void setGengxinUrl(String s)
    {
        gengxinurl = s;
    }

    private void shareClicked()
    {
        String[] strings = {"简单分享","高级分享"};
        new AlertDialog.Builder(getContext()).setItems(strings, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(i==0)
                {
                    String s = "我在兔喔喔我在忍者萌龟小游戏得了"+ getChengji()+ "分，过来一起挑战吧！下载地址："+gengxinurl;
                    share(s);
                }
                else
                {
                    String html = "";
                    html += "<html lang=\"zh-hans\"><head><title>兔喔喔</title><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/><link href=\"http://s1.techweb.com.cn/static/newwap/css/style.css?201709190936\" type=\"text/css\" rel=\"stylesheet\" rev=\"stylesheet\" media=\"screen\"/></head><body bgcolor=\"#ffaa43\"><div  class=\"content\"><div class=\"article_con\">";
                    String p = "";
                    p += "<p style=\"text-align: center;\"><br><br><br><br>我在兔喔喔忍者萌龟小游戏得了" + getChengji() + "分，过来一起挑战吧！<br><br><br><br></p>";
                    p += "<p style=\"text-align: center;\"><input type=\"button\" style = \"center\" value=\"下载兔喔喔APP\" onclick=\"javascrtpt:window.location.href='" + gengxinurl + "'\"></p>";
                    p += "<p style=\"text-align: center;\"<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></p><p></p><p></p><p></p><p></p><p></p>";
                    html += p;
                    html += "</div></body></html>";
                    File file = new File("/sdcard/jingpinwu/我在忍者萌龟小游戏得了" + getChengji() + "分，过来一起挑战吧.html");
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
                    shareFile(getContext(), file);
                }
            }
        }).create().show();
    }

    void share(String s)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }
}
