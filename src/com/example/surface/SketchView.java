package com.example.surface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.example.surface.MainActivity.TAG;

public class SketchView extends SurfaceView implements SurfaceHolder.Callback {
    
    private int[][] mWorldColor;
    private int mResolutionX = 16;//解像度  
    private int mResolutionY = 16;//解像度  
    private int mColorZ = Obj.Z;// 単位色（ 解像度は256/Z )
    
    private int mCenterX;
    private int mCenterY;
    private int mLengthX = 0;
    private int mLengthY = 0;
    private int mUnitX = 1;
    private int mUnitY= 1;
    private boolean mAdd = true;
    private Context mContext;
    private Vibrator mVibrator;

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        //setDrawingCacheEnabled(true);
        //setDrawingCacheBackgroundColor(Color.WHITE);
        setDimension(mResolutionX, mResolutionY);
        mContext = context;
        mCenterBlack.setStyle(Style.STROKE);
        mCenterBlack.setColor(Color.BLACK);
        mCenterBlack.setStrokeWidth(6.0f);
        mCenterBlack.setStrokeCap(Cap.ROUND);
        mCenterBlack.setAntiAlias(true);
        mVibrator = (Vibrator)context.getSystemService(Activity.VIBRATOR_SERVICE);
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
    
    public void post(final File file) throws ClientProtocolException, IOException {
        HttpPost request = new HttpPost("http://www.test.com");
        MultipartEntity entity = new MultipartEntity();
        //テキストの送信
        entity.addPart("name", new StringBody("値value",Charset.forName(HTTP.UTF_8)));
        //ファイルの送信
        entity.addPart("file", new FileBody(file,"application/octet-stream"));
        request.setEntity(entity);
        HttpClient client = new DefaultHttpClient();
        client.execute(request);
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

            int indexX0 = indexX;
            int indexY0 = indexY;
            int mX = (int) ev.getX();
            int mY = (int) ev.getY();
            indexX = mX / mUnitX;
            indexY = mY / mUnitY;
            int newColor = 0;
            
            if (indexX0 != indexX || indexY0 != indexY) {
                if (indexX < 16 && indexY < 16) {
                    if (mAdd) {
                        newColor = mWorldColor[indexX][indexY] + mColorZ;
                        mWorldColor[indexX][indexY] = Math.min(newColor, 255);
                    } else {
                        newColor = mWorldColor[indexX][indexY] - mColorZ;
                        mWorldColor[indexX][indexY] = Math.max(newColor, 0);
                    }
                    draw(); // 描画
                    mVibrator.vibrate(30);
                }
            }
        } else {
            indexX = -1;
            indexY = -1;
        }
        return true;
    }

    private final Paint mPaint = new Paint();
    private final Paint mCenterBlack = new Paint();

    public void draw() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            
            canvas.drawLine(mCenterX, mCenterY - 30, mCenterX, mCenterY + 30, mCenterBlack);
            canvas.drawLine(mCenterX - 30, mCenterY, mCenterX + 30, mCenterY, mCenterBlack);
            
            try {
                for (int ix = 0; ix < mWorldColor.length; ix++) {
                    for (int iy = 0; iy < mWorldColor[ix].length; iy++) {
                        int alpha = mWorldColor[ix][iy];
                        if (alpha == 0) continue;
                        mPaint.setColor(Color.argb(alpha, 0, 0, 0));
                        int x0 = ix * mUnitX;
                        int y0 = iy * mUnitY;
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
        mCenterX = width/2;
        mCenterY = height/2;
        mLengthX = width / mResolutionX;//300分割
        mLengthY = height / mResolutionY;//300分割
        mUnitX = width / mResolutionX;
        mUnitY = height / mResolutionY;
        draw();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}