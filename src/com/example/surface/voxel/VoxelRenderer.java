package com.example.surface.voxel;

import javax.microedition.khronos.opengles.GL10;

import com.example.surface.Obj;
import com.example.surface.R;
import com.example.surface.SketchView;

import rajawali.BaseObject3D;
import rajawali.lights.ALight;
import rajawali.lights.DirectionalLight;
import rajawali.materials.AMaterial;
import rajawali.materials.DiffuseMaterial;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.app.Activity;
import android.content.Context;

public class VoxelRenderer extends RajawaliRenderer {
    public static final int RESOLUTION = 16;

    public static final int BACKGROUND_COLOR = 0xffe6b73e;
    public static final int CYLINDER_COLOR = 0xff93d1cf;
    public static final int PLANE_COLOR = 0xffe6e6e6;
    public static final int OBJECT_COLOR = 0xfff4f4f4;
    public static final int OBJECT_UP_COLOR = 0xffffd1ff;
    public static final int OBJECT_DOWN_COLOR = 0xffa6ffa6;

    private ALight mLight;
    private AMaterial mMaterial;
    private BaseObject3D mObject;
    private BaseObject3D mObject2;
    private Cylinder mCylinder;
    private Plane mPlane;
    private AnimatedBar[][] mBars;
    private float mFling;

    public VoxelRenderer(Context context) {
        super(context);
        setFrameRate(30);
    }

    @Override
    public void initScene() {
        this.setBackgroundColor(BACKGROUND_COLOR);

        mLight = new DirectionalLight(1.0f, -0.5f, -0.5f);
        mLight.setColor(1.0f, 1.0f, 1.0f);
        mLight.setPower(1.5f);

        mMaterial = new DiffuseMaterial();
        mMaterial.setUseColor(true);

        mObject = new BaseObject3D();
        mObject2 = new BaseObject3D();
        mObject.addChild(mObject2);

        mCylinder = new Cylinder(1.42f, 2.0f / RESOLUTION);
        mCylinder.setZ((1.0f - RESOLUTION / 2) / RESOLUTION);
        mCylinder.setMaterial(mMaterial);
        mCylinder.setColor(CYLINDER_COLOR);
        mObject2.addChild(mCylinder);

        mPlane = new Plane(2.0f, 2.0f, 1, 1);
        mPlane.setZ((2.01f - RESOLUTION / 2) / RESOLUTION);
        mPlane.setMaterial(mMaterial);
        mPlane.setColor(PLANE_COLOR);
        mObject2.addChild(mPlane);

        mBars = new AnimatedBar[RESOLUTION][RESOLUTION];
        for (int i = 0; i < RESOLUTION; i++) {
            for (int j = 0; j < RESOLUTION; j++) {
                Cube bar = new Cube(2.0f / RESOLUTION);
                bar.setX((float) (i * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setY((float) ((RESOLUTION - j - 1) * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setZ((1.0f - RESOLUTION / 2) / RESOLUTION);
                bar.setZ((float) (1 - RESOLUTION / 2) / RESOLUTION);
                bar.setMaterial(mMaterial);
                bar.setColor(OBJECT_COLOR);
                mObject2.addChild(bar);
                mBars[i][j] = new AnimatedBar(bar);
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
                    mBars[i][j].animate((data[i][j] + 1) / Obj.Z);
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
