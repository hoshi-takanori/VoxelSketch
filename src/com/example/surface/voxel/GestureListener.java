package com.example.surface.voxel;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private VoxelFragment mFragment;

    public GestureListener(VoxelFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        mFragment.getRenderer().setFling(0);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        if (Math.abs(distanceX) >= Math.abs(distanceY)) {
            mFragment.getRenderer().addRotZ(distanceX / 4);
        } else {
            mFragment.getRenderer().addRotX(distanceY / 4);
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) >= Math.abs(velocityY)) {
            mFragment.getRenderer().setFling(- velocityX / 240);
        }
        return true;
    }
}
