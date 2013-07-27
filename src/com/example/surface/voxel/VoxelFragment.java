package com.example.surface.voxel;

import rajawali.RajawaliFragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.surface.R;

public class VoxelFragment extends RajawaliFragment {
    private VoxelRenderer mRenderer;

    public VoxelRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRenderer = new VoxelRenderer(getActivity());
        mRenderer.setSurfaceView(mSurfaceView);
        setRenderer(mRenderer);

        final GestureDetector detector = new GestureDetector(getActivity(), new GestureListener(this));
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = (FrameLayout) inflater.inflate(R.layout.voxel_fragment, container, false);
        mLayout.addView(mSurfaceView);
        return mLayout;
    }
}
