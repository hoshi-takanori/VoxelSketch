package com.example.surface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
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
    
    public final String save() {
        return Exporter.obj(mContext, mWorldColor);
    }
    
    public final void clear() {
        int lx = mWorldColor.length;
        int ly = mWorldColor[0].length;
        mWorldColor = new int[lx][ly];
        draw();
    }
    
    private class CadData {
        public Drawable img;
        public String cad;
    }
    public final void loadCads() {
        AsyncTask<String, String, ArrayList<CadData>> task = new AsyncTask<String, String, ArrayList<CadData>>() {
            private ArrayList<Drawable> list;

            @Override
            protected ArrayList<CadData> doInBackground(String... params) {
                String raw="";
                try {
                    String json = loadContent("http://sketchvoxel.appspot.com/list");
                    JSONArray arr = new JSONArray(json);
                    Log.i(TAG, "JSONARR: " + arr);
                    ArrayList<Drawable> list = new ArrayList<Drawable>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject j = arr.getJSONObject(i);
                        String url0 = j.getString("img");
                        URL url = new URL(url0);
                        InputStream istream = url.openStream();
                        Drawable d = Drawable.createFromStream(istream, "img:"+i);
                        istream.close();
                        CadData cad = new CadData();
                        cad.img = d;
                        cad.cad = j.getString("cad");
                    }
                    //loadCad(raw);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(ArrayList<CadData> list) {
                int[] ids = {R.id.thumb0, R.id.thumb1, R.id.thumb2, R.id.thumb3};
                for (int i = 0; i < ids.length; i++) {
                    
                }
            }
            
        };
        task.execute("");
    }
    
    private final String ROW = "=";
    private final String COLUMN = ",";
    private final void loadCad(String raw) {
        boolean init = false;
        String[] rows = raw.split(ROW);
        for (int i = 0; i < rows.length; i++) {
            String[] columns = rows[i].split(COLUMN);
            if (!init) {
                mWorldColor = new int[rows.length][columns.length];
                init = true;
            }
            for (int j = 0; j < columns.length; j++) {
                mWorldColor[i][j] = Integer.valueOf(columns[j]);
            }
        }
    }
    
    private final String cad() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mWorldColor.length; i++) {
            for (int j = 0; j < mWorldColor[i].length; j++) {
                sb.append(mWorldColor[i][j]).append(COLUMN);
            }
            sb.append(ROW);
        }
        sb.delete(sb.length()-2, sb.length());//the end must be ",=" 
        return sb.toString();
    }
    
    public final void store() {
        // Bitmapの作成
        Bitmap bitmap = Bitmap.createBitmap(1200, 1200, Bitmap.Config.ARGB_4444);
         
        // Canvasの作成:描画先のBitmapを与える
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
         
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
        
        String filename = "tmp.png"; 
        try {
            FileOutputStream fo = mContext.openFileOutput(filename, Activity.MODE_WORLD_READABLE);
            bitmap.compress(CompressFormat.PNG, 100, fo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            String path = mContext.getFilesDir().getCanonicalPath() + "/" + filename;
            File file = new File(path);
            String cad = cad();
            String obj = save();
            post(file, cad, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private final String loadContent(String url) throws IOException {
        URL url0 = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(url0.openStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();
        return sb.toString();
    }
    
    public void post(final File file, final String cad, final String obj) throws ClientProtocolException, IOException {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... params) {
                try {
                    String content = loadContent("http://sketchvoxel.appspot.com/store");
                    JSONObject json = new JSONObject(content);
                    String postUrl = json.getString("url");
                    String code = json.getString("code");
                    
                    Log.i(TAG, "code: " + code + ", url: " + postUrl);
                    //HttpPost request = new HttpPost("http://sketchvoxel.appspot.com/store");
                    HttpPost request = new HttpPost(postUrl);
                    MultipartEntity entity = new MultipartEntity();
                    //テキストの送信
                    entity.addPart("cad", new StringBody(cad,Charset.forName(HTTP.UTF_8)));
                    entity.addPart("obj", new StringBody(obj,Charset.forName(HTTP.UTF_8)));
                    //ファイルの送信
                    entity.addPart("img", new FileBody(file,"application/octet-stream"));
                    request.setEntity(entity);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse res = client.execute(request);
                    res.getEntity();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        
        task.execute("","","");

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

    public int[][] getWorldColor() {
        return mWorldColor;
    }
}