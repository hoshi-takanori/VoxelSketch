package com.example.surface.voxel;

import javax.microedition.khronos.opengles.GL10;

import com.example.surface.Obj;
import com.example.surface.R;
import com.example.surface.SketchView;

import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Quaternion;
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
    private Cylinder mCylinder;
    private Cube[][] mBars;
    private Quaternion mOrientation;
    private Quaternion mFling;

    public VoxelRenderer(Context context) {
        super(context);
        setFrameRate(30);
        initOrientation();
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

        mCylinder = new Cylinder(1.42f, 2.0f / RESOLUTION);
        mCylinder.setZ((0.99f - RESOLUTION / 2) / RESOLUTION);
        mCylinder.setMaterial(mMaterial);
        mCylinder.setColor(CYLINDER_COLOR);
        mObject.addChild(mCylinder);

        mBars = new Cube[RESOLUTION][RESOLUTION];
        for (int i = 0; i < RESOLUTION; i++) {
            for (int j = 0; j < RESOLUTION; j++) {
                Cube bar = new Cube(2.0f / RESOLUTION);
                bar.setX((float) (j * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setY((float) (i * 2 - RESOLUTION + 1) / RESOLUTION);
                bar.setZ((1.0f - RESOLUTION / 2) / RESOLUTION);
                bar.setMaterial(mMaterial);
                bar.setColor(OBJECT_COLOR);
                mBars[i][j] = bar;
                mObject.addChild(bar);
            }
        }

        mObject.addLight(mLight);
        addChild(mObject);

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

        if (mFling != null) {
            multiplyOrientation(mFling);
        }
        mObject.setOrientation(mOrientation);
        super.onDrawFrame(glUnused);
    }

    public void initOrientation() {
        mOrientation = new Quaternion();
    }

    public void multiplyOrientation(Quaternion quaternion) {
        mOrientation.multiply(quaternion);
    }

    public void setFling(Quaternion fling) {
        mFling = fling;
    }
}
