package com.example.surface;

import java.util.UUID;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.example.surface.MainActivity.TAG;

public class SketchView extends SurfaceView implements SurfaceHolder.Callback {
    
    private int[][] mWorldColor;
    private int mResolutionX = 16;//解像度  
    private int mResolutionY = 16;//解像度  
    private int mColorZ = Obj.Z;// 単位色（ 解像度は256/Z )
    
    private int mCanvasWidth;
    private int mCanvasHeight;
    private int mLengthX = 0;
    private int mLengthY = 0;
    private int mUnitX = 1;
    private int mUnitY= 1;
    private boolean mAdd = true;
    private int mX;
    private int mY;
    private Context mContext;

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setDrawingCacheEnabled(true);
        setDrawingCacheBackgroundColor(Color.WHITE);
        setDimension(mResolutionX, mResolutionY);
        mContext = context;
    }

    public final boolean toggleMode() {
        mAdd = !mAdd;
        return mAdd;
    }
    public final void modePen() {
        mAdd = true;
    }
    
    public final void modeEraser() {
        mAdd = false;
    }
    
    public final boolean isModePen() {
        return mAdd;
    }
    
    public final void setDimension(int lx, int ly) {
        mWorldColor = new int[lx][ly];
    }
    
    public final void save() {
        Exporter.obj(mContext, mWorldColor);
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        draw();
    }

    private int indexX;
    private int indexY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN ||
            action == MotionEvent.ACTION_MOVE) {
            // タッチした座標を取得
            mX = (int) ev.getX();
            mY = (int) ev.getY();
            Log.i(TAG, mX + ", " + mY);
            draw(); // 描画
        } else {
            indexX = -1;
            indexY = -1;
        }
        return true;
    }

    private final Paint mPaint = new Paint();
    public void draw() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            //canvas.drawColor(Color.WHITE);
            int indexX0 = indexX;
            int indexY0 = indexY;
            indexX = mX / mUnitX;
            indexY = mY / mUnitY;
            int x0 = indexX * mUnitX;//着色のpoint
            int y0 = indexY * mUnitY;//着色のpoint
            try {
                int newColor = 0;
                if (mAdd) {
                    newColor = mWorldColor[indexX][indexY] + mColorZ;
                } else {
                    newColor = mWorldColor[indexX][indexY] - mColorZ;
                }

                if (indexX0 != indexX || indexY0 != indexY) {
                    if (mAdd) {
                        mWorldColor[indexX][indexY] = Math.min(newColor, 255);
                    } else {
                        mWorldColor[indexX][indexY] = Math.max(newColor, 0);
                    }
                }
                for (int ix = 0; ix < mWorldColor.length; ix++) {
                    for (int iy = 0; iy < mWorldColor[ix].length; iy++) {
                        int alpha = Math.max(255 - mWorldColor[ix][iy], 0);
                        mPaint.setColor(Color.rgb(alpha, alpha, alpha));
                        //mPaint.setColor(Color.argb(alpha, 0, 0, 0));
                        x0 = ix * mUnitX;
                        y0 = iy * mUnitY;
                        canvas.drawRect(x0, y0, x0+mLengthX, y0+mLengthY, mPaint);
                    }
                }
            
            } catch (Exception e){
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        mLengthX = width / mResolutionX;//300分割
        mLengthY = height / mResolutionY;//300分割
        mUnitX = width / mResolutionX;
        mUnitY = height / mResolutionY;
        draw();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}