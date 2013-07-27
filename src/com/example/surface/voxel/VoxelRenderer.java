package com.example.surface.voxel;

import javax.microedition.khronos.opengles.GL10;

import com.example.surface.Obj;
import com.example.surface.R;
import com.example.surface.SketchView;

import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.primitives.Cube;
import rajawali.renderer.RajawaliRenderer;
import android.app.Activity;
import android.content.Context;

public class VoxelRenderer extends RajawaliRenderer {
    public static final int RESOLUTION = 16;

    public static final int BACKGROUND_COLOR = 0xffd1a942;
    public static final int CYLINDER_COLOR = 0xffff00ff;
    public static final int OBJECT_COLOR = 0xff00ffff;

    private DirectionalLight mLight;
    private DiffuseMaterial mMaterial;
    private BaseObject3D mObject;
    private BaseObject3D mObject2;
    private Cylinder mCylinder;
    private Cube[][] mBars;
    private float mFling;

    public VoxelRenderer(Context context) {
        super(context);
        setFrameRate(30);
    }

    @Override
    public void initScene() {
        this.setBackgroundColor(BACKGROUND_COLOR);

        mLight = new DirectionalLight(1f, 0.2f, -1.0f);
        mLight.setColor(1.0f, 1.0f, 1.0f);
        mLight.setPower(2);

        mMaterial = new DiffuseMaterial();
        mMaterial.setUseColor(true);

        mObject = new BaseObject3D();
        mObject2 = new BaseObject3D();
        mObject.addChild(mObject2);

        mCylinder = new Cylinder(1.42f, 2.0f / RESOLUTION);
        mCylinder.setZ((0.99f - RESOLUTION / 2) / RESOLUTION);
        mCylinder.setMaterial(mMaterial);
        mCylinder.setColor(CYLINDER_COLOR);
        mObject2.addChild(mCylinder);

        mBars = new Cube[RESOLUTION][RESOLUTION];
        for (int i = 0; i < RESOLUTION; i++) {
            for (int j = 0; j < RESOLUTION; j++) {
                Cube bar = new Cube(2.0f / RESOLUTION);
                bar.setX((float) (i * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setY((float) ((RESOLUTION - j - 1) * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setZ((1.0f - RESOLUTION / 2) / RESOLUTION);
                bar.setMaterial(mMaterial);
                bar.setColor(OBJECT_COLOR);
                mBars[i][j] = bar;
                mObject2.addChild(bar);
            }
        }

        mObject.addLight(mLight);
        addChild(mObject);
        initOrientation();

        mCamera.setZ(3.6f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        SketchView sketchView = (SketchView) ((Activity) mContext).findViewById(R.id.sketch_view);
        if (sketchView != null) {
            int[][] data = sketchView.getWorldColor();
            for (int i = 0; i < RESOLUTION; i++) {
                for (int j = 0; j < RESOLUTION; j++) {
                    int z = (data[i][j] + 1) / Obj.Z;
                    mBars[i][j].setZ((float) (z + 1 - RESOLUTION / 2) / RESOLUTION);
                    mBars[i][j].setScaleZ(z + 1);
                }
            }
        }

        addRotZ(mFling);
        super.onDrawFrame(glUnused);
    }

    public void initOrientation() {
        mObject.setRotX(75);
        mObject2.setRotZ(0);
        mFling = 0;
    }

    public void addRotX(float delta) {
        float rotX = mObject.getRotX() + delta;
        mObject.setRotX(Math.max(Math.min(rotX, 100), 0));
    }

    public void addRotZ(float delta) {
        float rotZ = mObject2.getRotZ() + delta;
        mObject2.setRotZ(rotZ % 360);
    }

    public void setFling(float fling) {
        mFling = fling;
    }
}
