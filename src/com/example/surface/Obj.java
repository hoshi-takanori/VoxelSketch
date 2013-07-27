package com.example.surface;

import android.util.Log;
import android.util.SparseIntArray;
import static com.example.surface.MainActivity.TAG;
public class Obj {
    //単位ボックスの座標
    public final static int V[][] = {{0,0,0},{1,0,0},{0,1,0},{1,1,0},{0,0,1},{1,0,1},{0,1,1},{1,1,1}};
    //単位ボックスの三角ポリゴン出力のリスト(座標1, 座標2, 座標3, 法線ベクトルindex)
    public final static int F[][] = {{1,3,2,1},{2,3,4,1},{1,5,3,2},{3,5,7,2},{3,7,4,3},{4,7,8,3},{1,2,5,4},{2,6,5,4},{2,4,6,5},{4,8,6,5},{5,6,7,6},{6,8,7,6}};
    public static int L = 3;//ボックスの1辺の長さ
    public static int Z = 32;
   
    public static String head(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("g %s\n", name));
        sb.append(String.format("vn 0 0 -1\n"));
        sb.append(String.format("vn -1 0 0\n"));
        sb.append(String.format("vn 0 1 0\n"));
        sb.append(String.format("vn 0 -1 0\n"));
        sb.append(String.format("vn 1 0 0\n"));
        sb.append(String.format("vn 0 0 1\n"));
        return sb.toString();
    }
    public static void removeFace(Obj box1, Obj box2) {
        //vector = (p2[0]-p1[0], p2[1]-p1[1], p2[2]-p1[2])
        final Point3D p1 = box1.point;
        final Point3D p2 = box2.point;
        int vX = p2.x - p1.x;
        int vY = p2.y - p1.y;
        int vZ = p2.z - p1.z;
        int faceIndex = 0;
        if (vX == 1 && vY == 0 && vZ == 0) {
            faceIndex = 5;
        } else if (vX == 0 && vY == 1 && vZ == 0) {
            faceIndex = 3;
        } else if (vX == 0 && vY == 0 && vZ == 1) {
            faceIndex = 6;
        } else if (vX == -1 && vY == 0 && vZ == 0) {
            faceIndex = 2;
        } else if (vX == 0 && vY == -1 && vZ == 0) {
            faceIndex = 4;
        } else if (vX == 0 && vY == 0 && vZ == -1) {
            faceIndex = 1;
        }
        if (0 < faceIndex) {
            Log.i(TAG, "Remove faceIndex=" + faceIndex + "/" + box1 + ", " + box2);
            box1.addFace(faceIndex);
            box2.addFace(7 - faceIndex);
        }
            
    }

    public Point3D point;
    private final SparseIntArray face = new SparseIntArray();
    public Obj(Point3D p) {
        point = p;
    }
    
    public Obj(int x, int y, int z) {
        point = new Point3D();
        point.x = x;
        point.y = y;
        point.z = z;
    }
    
    //共有面の面番号を保存
    public void addFace(int index) {
        face.put(index, index);
    }
    
    public int[][] around() {
        int x = point.x;
        int y = point.y;
        int z = point.z;
        int[][] tmp = {{x-1,y,z},{x,y-1,z},{x,y,z-1},{x+1,y,z},{x,y+1,z},{x,y,z+1}};
        return tmp;
    }
    
    /**
     * 2点間の距離を計算する
     * @param box
     * @return 距離
     */
    public int distance(Point3D box) {
        final Point3D b = point;
        return (box.x - b.x) + (box.y - b.y) + (box.z - b.z);
    }
    
    /**
     * 隣り合っているかをチェック
     * @param box
     * @return true:隣り合っている, false:隣り合っていない
     */
    public boolean nearby(Point3D box) {
        int d = distance(box);
        if (d == -1 || d == 1) {
            return true;
        } else {
            return false;
        }
    }
    
    public String dump(int number) {
        Point3D b = point;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("#v-%d\n", number));
        for (int i[] : V){
            sb.append(String.format("v %d %d %d\n", (i[0]+b.x)*L, (i[1]+b.y)*L, (i[2]+b.z)*L));
            //file.write("v {x} {y} {z}\n".format( x=((i[0]+b[0])*L), y=((i[1]+b[1])*L), z=((i[2]+b[2])*L) ))
        }
            
        sb.append(String.format("#f-%d\n", number));
        //file.write('#f-{n}\n'.format(n=number))
        int num = 8 * number;
        for (int[] i : F) {
            int faceIndex = i[3];
            if (face.get(faceIndex, -1) == -1) {
                sb.append(String.format("f %d//%d %d//%d %d//%d\n", i[0]+num,faceIndex, i[1]+num,faceIndex, i[2]+num,faceIndex));
            }
        }
        return sb.toString();
    }
    
    public String toString() {
        return "(" + point.x + ", " + point.y + ", " + point.z + ")";
    }
    
    public final class Point3D {
        public Object point;
        public int x;
        public int y;
        public int z;
        public boolean equal(Point3D p) {
            return x == p.x && y == p.y && z == p.z;
        }
    }
}
