package ru.katarsis.digitrain;

/**
 * Created by piton on 15.02.2016.
 */
import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MatrixDigitRain extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };
        Paint normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint whitePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String TEXT = new String("あたアカサザジズゼゾシスセソキクケコイウエオジャな0123456789");
        int width;
        int height;
        int columnCount =0;
        int latestIndexY[];
        int FONT_SIZE =10;
        String wordMatrix[] ;
        int ttlMatrix[][];
        Random random = new Random();
        Bitmap map;

        private boolean visible = true;
        private boolean touchEnabled;

        public MyWallpaperEngine() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MatrixDigitRain.this);

            touchEnabled = prefs.getBoolean("touch", false);

            handler.post(drawRunner);

            yellowPaint.setTextSize(FONT_SIZE);
            yellowPaint.setStyle(Paint.Style.STROKE);
            yellowPaint.setColor(Color.rgb(243, 243, 21));

            normalPaint.setTextSize(FONT_SIZE);
            //normalPaint.setStyle(Paint.Style.STROKE);
            normalPaint.setColor(Color.rgb(0, 200, 0));

            whitePaint.setTextSize(FONT_SIZE);
            whitePaint.setStyle(Paint.Style.STROKE);
            whitePaint.setColor(Color.rgb(255,255,255));
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }




    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        super.onSurfaceDestroyed(holder);
        this.visible = false;
        handler.removeCallbacks(drawRunner);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format,int width, int height) {
        this.width = width;
        this.height = height;
        this.columnCount = width/FONT_SIZE;
        this.latestIndexY = new int[columnCount];
        for(int i=0;i<columnCount;i++){
            this.latestIndexY[i]=0;
        }
        this.wordMatrix = new String[columnCount];

        map =  Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        super.onSurfaceChanged(holder, format, width, height);
    }



    @Override
    public void onTouchEvent(MotionEvent event) {
        if (touchEnabled) {

           SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                     drawText(canvas);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            super.onTouchEvent(event);
        }
    }

    private void draw() {
        SurfaceHolder holder = getSurfaceHolder();
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
               drawText(canvas);
            }
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
        handler.removeCallbacks(drawRunner);
        if (visible) {
            handler.postDelayed(drawRunner, 50);
        }
    }

    // Surface view requires that all elements are drawn completely
    private void drawText(Canvas canvas) {
        try {
            Canvas bufferCanvas = new Canvas(map);
            bufferCanvas.drawColor(Color.argb(20, 0, 0, 0));
            for (int i=0;i<columnCount;i++) {
                int index = TEXT.length();
                String tmpStr = String.valueOf(TEXT.charAt(random.nextInt(index)));

                if(wordMatrix[i]!=null){
                    bufferCanvas.drawText(wordMatrix[i], i*FONT_SIZE, latestIndexY[i] * FONT_SIZE, normalPaint);
                }
                wordMatrix[i] = tmpStr;
                bufferCanvas.drawText(tmpStr, i*FONT_SIZE, (latestIndexY[i]+1)*FONT_SIZE, whitePaint);
                latestIndexY[i]++;

                if(latestIndexY[i]*FONT_SIZE>height&&random.nextFloat()>0.95){
                    latestIndexY[i]=0;
                }
            }

            canvas.drawBitmap(map,0,0,null);

            /*
            for (int x = 0; x < columnCount; x++) {
                for (int y = 0; y < latestIndexY[x]; y++) {
                    Log.i("DRAWING","x:"+x+" y:"+y);
                    Log.i("CONTENT",wordMatrix[x][y]);
                    canvas.drawText(wordMatrix[x][y], x * 40, y * 40, normalPaint);
                }
            }*/
        }catch (Exception exp){
            exp.printStackTrace();
        }

    }
    }
}
