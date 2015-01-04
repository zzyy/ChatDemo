package com.zy.slidemenu.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.zy.slidemenu.R;

/**
 * TODO: document your custom view class.
 */
public class SlideMenu extends HorizontalScrollView {
    private static final String TAG = "zy";

    private int mScreenWidth;

    private int mLeftMenuWidth = 200;    //50dp
    private int mLeftMenuWidthPx;

    private boolean isOpen;

    private boolean once;
    private VelocityTracker mVelocityTracker;


    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        //init data
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mScreenWidth = outMetrics.widthPixels;
        mLeftMenuWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLeftMenuWidth, outMetrics);
        Log.d(TAG, String.format("mScreenWidth=%d; mLeftMenuWidthPx=%d", mScreenWidth, mLeftMenuWidthPx));

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SlideMenu, defStyle, 0);
        int N = a.getIndexCount();
        for (int i =0; i<N; i++){
            switch (a.getIndex(i)){
                case R.styleable.SlideMenu_menuWidth:
                    mLeftMenuWidthPx = (int) a.getDimension(R.styleable.SlideMenu_menuWidth, mLeftMenuWidthPx);
            }
        }

        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup leftMenu = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(1);

            leftMenu.getLayoutParams().width = mLeftMenuWidthPx;
            content.getLayoutParams().width = mScreenWidth;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            scrollTo(mLeftMenuWidthPx, 0);
            once=true;
        }
    }

        float velocityX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();

        mVelocityTracker.addMovement(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                velocityX = mVelocityTracker.getXVelocity();
                mVelocityTracker.clear();
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                int scrollX = getScrollX();
                Log.d(TAG, "velocityX=" + velocityX +"; scrollX="+ scrollX +"; isOpen="+isOpen);
                if ((scrollX > mLeftMenuWidthPx/2 || velocityX < -4000) && isOpen){
                    closeMenu();
                }else if ((scrollX <= mLeftMenuWidthPx/2 || velocityX > 4000) && !isOpen) {
                    openMenu();
                }else if (scrollX > mLeftMenuWidthPx/2){
                    closeMenu();
                }else if (scrollX <= mLeftMenuWidthPx/2){
                    openMenu();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
        }

        return super.onTouchEvent(ev);
    }

    public void openMenu(){
        Log.d(TAG, "openMenu");
        smoothScrollTo(0, 0);
        isOpen = true;
    }

    public void closeMenu() {
        Log.d(TAG, "closeMenu");
        smoothScrollTo(mLeftMenuWidthPx, 0);
        isOpen = false;
    }

    public void toggleMenu(){
        if (isOpen)
            closeMenu();
        else
            openMenu();
    }
}
