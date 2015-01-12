package com.zy.bouncescroll.ui;

import android.util.Log;
import android.view.animation.Interpolator;

/**
 * Created by Simon on 2015/1/8.
 */
public class OverScrollerInterpolation implements Interpolator {
    private static final String TAG ="zy";

    @Override
    public float getInterpolation(float input) {
        Log.d(TAG, "input=" +input);

        return input;
    }
}
