package com.example.surface.voxel;

import rajawali.BaseObject3D;

public class AnimatedBar {
    public static final int MAX_COUNT = 8;

    private BaseObject3D bar;
    private float curHeight;
    private int newHeight;
    private int count;

    public AnimatedBar(BaseObject3D bar) {
        this.bar = bar;
    }

    public void animate(int height) {
        if (height != newHeight) {
            newHeight = height;
            count = MAX_COUNT;
        }

        if (count == 0) {
            return;
        }

        if (count > 1) {
            curHeight += (newHeight - curHeight) / count;
        } else {
            curHeight = newHeight;
        }

        bar.setZ((float) (curHeight + 1 - VoxelRenderer.RESOLUTION / 2) / VoxelRenderer.RESOLUTION);
        bar.setScaleZ(curHeight + 1);

        if (count > 1) {
            if (newHeight > curHeight) {
                bar.setColor(VoxelRenderer.OBJECT_UP_COLOR);
            } else {
                bar.setColor(VoxelRenderer.OBJECT_DOWN_COLOR);
            }
        } else {
            bar.setColor(VoxelRenderer.OBJECT_COLOR);
        }

        count--;
    }
}
