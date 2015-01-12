package com.zy.bouncescroll.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by Simon on 2015/1/6.
 */
public class BounceScrollView2 extends ScrollView {
    private VelocityTracker mVelocityTracker;

    protected Context mContext;

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;

    private static final int INVALID_POINTER = -1;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;

    private OverScroller mScroller;
    private int mLastMotionY;
    private static final String TAG = "zy";

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int mOverscrollDistance = 800;
    private int mOverflingDistance;
    private float mFricrion;

    public BounceScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public BounceScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init(){
        Interpolator interpolator = new Interpolator(){
            @Override
            public float getInterpolation(float input) {
                Log.d(TAG, "input=" +input);
                return input;
            }
        };

        mScroller = new OverScroller(mContext, interpolator);

        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop();

        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

//        mFricrion = configuration.getScrollFriction();

        Log.d(TAG, "init");
        Log.d(TAG, "mTouchSlop="+mTouchSlop +"; mMinimumVelocity="+mMinimumVelocity +"; mMaximumVelocity="+mMaximumVelocity + "; friction=" + mFricrion);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged){
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);

                mIsBeingDragged = !mScroller.isFinished();
            }
            break;

            case MotionEvent.ACTION_MOVE:{
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER){
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1){
                    break;
                }

                final int y = (int) ev.getY(pointerIndex);

                final int dy = y - mLastMotionY;

                if (Math.abs(dy) > mTouchSlop){
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                    initVelocityTrackerIfNotExists();
                    mVelocityTracker.addMovement(ev);

                }


            }
            break;

        }


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG, "ACTION_DOWN, mIsBeingDragged=" + mIsBeingDragged +"; mScroller.isFinished()=" +mScroller.isFinished());

                if (getChildCount() == 0){
                    return false;
                }
                if ((mIsBeingDragged = !mScroller.isFinished())) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }

                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
            }
            break;

            case MotionEvent.ACTION_MOVE:{
                //获取同一个pointer
                final int activePointIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointIndex == -1){
                    break;
                }

                int y = (int) ev.getY(activePointIndex);
                //先下划为负数, 即mScroller滑动负的, 界面向下移动
                int deltaY = mLastMotionY -y;

                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop){
                    final ViewParent parent = getParent();
                    if (parent != null){
                        parent.requestDisallowInterceptTouchEvent(true);
                    }

                    mIsBeingDragged = true;
                    if (deltaY < 0){
                        deltaY -= mTouchSlop;
                    }else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBeingDragged){
                    mLastMotionY = y;
                    final int oldX = getScrollX();
                    final int oldY = getScrollY();
                    final int range = getScrollRange();

//                Log.d(TAG, "ACTION_MOVE; mIsBeingDragged=" + mIsBeingDragged + "; deltaY=" +deltaY +" range=" +range);
                    final int overscrollMode = getOverScrollMode();
                    final boolean canOverscroll = overscrollMode == OVER_SCROLL_ALWAYS ||
                            (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && range > 0);

                    //在边界之外
                    if (mScroller.isOverScrolled()){
                        deltaY = (int) (0.5*deltaY);
                    }
//                    Log.d(TAG, "deltaY=" + deltaY +"; isOverScrolled()=" + mScroller.isOverScrolled());

                    if (overScrollBy(0, deltaY, getScrollX(), getScrollY(), 0, range, 0, mOverscrollDistance, true)){
                        mVelocityTracker.clear();
                    }

                    if (canOverscroll) {
                        final int pulledToY = oldY + deltaY;
                        if (pulledToY < 0) {
                            //到上边界时效果

                        } else if (pulledToY > range) {
                            //到达下边界效果

                        }
                    }
                }

            }
            break;

            case MotionEvent.ACTION_UP:{
                Log.d(TAG, "ACTION_UP, mIsBeingDragged=" + mIsBeingDragged +"; getScrollY=" + getScrollY());
                if (mIsBeingDragged){
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                    final int velocityY = (int) velocityTracker.getYVelocity(mActivePointerId);
                    Log.d(TAG, "velocityY=" + velocityY +"; mMinimumVelocity=" + mMinimumVelocity);

                    if (getChildCount() >0 ){
                        if (Math.abs(velocityY) >= mMinimumVelocity ){
                            fling(-velocityY);

                        }else{
                            if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0,
                                    getScrollRange())) {
                                postInvalidateOnAnimation();
                            }
                        }
                    }

                }

                mActivePointerId = this.INVALID_POINTER;
                endDrag();
            }
            break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL");
                if (mIsBeingDragged && getChildCount() > 0) {
                    if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                        postInvalidateOnAnimation();
                    }
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.d(TAG, "ACTION_POINTER_DOWN");
                final int index = ev.getActionIndex();
                mLastMotionY = (int) ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP");
                mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }


        return true;
    }




    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

            final int oldX = getScrollX();
            final int oldY = getScrollY();

            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            int rangeY = getScrollRange();
//Log.d(TAG, "computeScroll(); getScrollY=" + getScrollY() +"; isOverScrolled=" + mScroller.isOverScrolled() +"; mScroller.getCurrY()=" +mScroller.getCurrY());
//            Log.d(TAG, "" + mScroller.getf)
            if (oldY != y || oldX != x){

                overScrollBy(x - oldX, y - oldY, oldX, oldY, 0, rangeY, 0, mOverscrollDistance, false);
            }


        }

        super.computeScroll();
    }

    private void endDrag() {
        mIsBeingDragged = false;

        recycleVelocityTracker();

    }
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
        return scrollRange;
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            int bottom = getChildAt(0).getHeight();

            Log.d(TAG, "height="+height + "; bottom=" +bottom);
            mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0,
                    Math.max(0, bottom - height), 0, height / 2);

            postInvalidateOnAnimation();
        Log.d(TAG, "fling, velocityY=" + velocityY + "; maxY=" + Math.max(0, bottom - height) + "; overY=" + height / 2);
        }
    }
}
