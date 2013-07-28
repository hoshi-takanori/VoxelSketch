package com.example.surface;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SketchFragment extends Fragment {
    private SketchView mSketchView;
    private OnClickListener mClick = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.mode_draw_btn) {
                mDrawBtn.setBackgroundResource(R.drawable.b_draw_on);
                mDeleteBtn.setBackgroundResource(R.drawable.btn_delete);
                mSketchView.modePen();
            } else if (id == R.id.mode_delete_btn) {
                mDrawBtn.setBackgroundResource(R.drawable.btn_draw);
                mDeleteBtn.setBackgroundResource(R.drawable.b_delete_on);
                mSketchView.modeEraser();
            } else if (id == R.id.new_btn) {
                //mSketchView.save();
                mSketchView.png();
            }
        }
    };
    private Button mDrawBtn;
    private Button mDeleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sketch_fragment, container, false);
        mDrawBtn = (Button)layout.findViewById(R.id.mode_draw_btn);
        mDeleteBtn = (Button)layout.findViewById(R.id.mode_delete_btn);
        mDrawBtn.setOnClickListener(mClick);
        mDeleteBtn.setOnClickListener(mClick);
        View btn = layout.findViewById(R.id.new_btn);
        btn.setOnClickListener(mClick);
        btn = layout.findViewById(R.id.gallery_btn);
        btn.setOnClickListener(mClick);
        mSketchView = (SketchView)layout.findViewById(R.id.sketch_view);
        return layout;
    }
}
