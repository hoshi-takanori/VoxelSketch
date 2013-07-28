package com.example.surface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import static com.example.surface.MainActivity.TAG;

public class Exporter {
    
    public final static String obj(Context act, int[][] map) {
        int lenX = map.length;
        int lenY = map[0].length;
        int lenZ = 256 / Obj.Z;
        LinkedList<Obj> list = new LinkedList<Obj>();
        
        /**
         * 1. XYZ座標にオブジェクトを配置
         */
        final Obj[][][] world = new Obj[lenX][lenY][lenZ];
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int color = map[x][y];
                if (color != 0) {
                    int z = color / Obj.Z;
                    for (int iz = 0; iz < z; iz++) {
                        Obj o = new Obj(x, y, iz);
                        world[x][y][iz] = o;
                        list.add(o);
                    }
                }
            }
        }
        
        /**
         * 2. Over shellの削除
         */
        for (Obj o : list) {
            int[][] around = o.around();//周辺座標の取得
            for (int[] a : around) {
                int x = a[0];
                int y = a[1];
                int z = a[2];
                if (0 <= x && x < lenX && 0 <= y && y < lenY && 0 <= z && z < lenZ) {//周辺座標が描画領域内
                    Obj box = world[x][y][z];
                    if (box != null) {//周辺座標にboxがある場合は隣り合っている
                        Obj.removeFace(o, box);
                    }
                }
            }
        }
        
        /**
         * 3. CADデータの書き出し
         */
        Log.i(TAG, "" + Util.checkExternalStorageAvaiable());
        Log.i(TAG, "" + Util.checkExternalStorageWritable());
        
        String path = Environment.getExternalStorageDirectory().getPath();
        String filename = path + "/tmp.obj";
        Log.i(TAG, "PATH====" + filename);
        File file = new File(filename);
        
        //File file = new File(act.getExternalFilesDir(null), filename);
        //File file = new File(path, filename);
        //file.getParentFile().mkdir();
        StringBuilder objStr = new StringBuilder();
        FileOutputStream fos;
        try {
            fos = act.openFileOutput("tmp.obj", Activity.MODE_WORLD_READABLE);
            //fos = new FileOutputStream(file, true);
            //fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            String str = Obj.head(filename);
            bw.write(str);
            bw.flush();
            objStr.append(str);
            //Log.i(TAG, str);
            for (int number = 0; number < list.size(); number++) {
                Obj o = list.get(number);
                str = o.dump(number);
                bw.write(str);
                bw.flush();
                objStr.append(str);
            }
            
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objStr.toString();
    }
}
