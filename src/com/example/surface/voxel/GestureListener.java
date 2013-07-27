package com.example.surface.voxel;

import rajawali.math.Quaternion;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	private VoxelFragment mFragment;

	public GestureListener(VoxelFragment fragment) {
		mFragment = fragment;
	}

	public Quaternion calcQuaternion(MotionEvent event, float dx, float dy, float d) {
		float r = FloatMath.sqrt(dx * dx + dy * dy);
		float x = event.getX() - (mFragment.getRenderer().getViewportWidth() / 2);
		float y = event.getY() - (mFragment.getRenderer().getViewportHeight() / 2);
		float s = FloatMath.sqrt(x * x + y * y);
		float cos = FloatMath.cos(r / d / 2);
		float sin = FloatMath.sin(r / d / 2);
		if (s < 16) {
			return new Quaternion(cos, sin * dy / r, sin * dx / r, 0);
		} else {
			float p = (dx * x + dy * y) / (r * s);
			float q = (dx * y - dy * x) / (r * s);
			Quaternion quaternion = new Quaternion(cos, sin * (dy / r) * p * p, sin * (dx / r) * p * p, sin * q * q * (q >= 0 ? 1 : -1));
			quaternion.normalize();
			return quaternion;
		}
	}

	@Override
	public boolean onDown(MotionEvent event) {
		mFragment.getRenderer().setFling(null);
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
		mFragment.getRenderer().multiplyOrientation(calcQuaternion(event2, distanceX, distanceY, 120));
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
		mFragment.getRenderer().setFling(calcQuaternion(event2, - velocityX, - velocityY, 6000));
		return true;
	}
}
