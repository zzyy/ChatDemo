package com.zy.imageloader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.zy.imageloader.R;

/**
 * TODO: document your custom view class.
 */
public class ZoomImageView extends View {
    private static final String TAG = "zy";

    //状态
    private static final int STATUS_INIT = 0x01;        //默认状态
    private static final int STATUS_ZOOM_OUT = 0x02;    //放大
    private static final int STATUS_ZOOM_IN = 0x04;        //缩小
    private static final int STATUS_MOVE = 0x08;        //移动
    private static final int STATUS_MASK = 0x0F;        //用于 & 计算Status

    private int currentStatus;

    //用于图形变换的矩阵
    Matrix matrix = new Matrix();

    //图片实际宽高
    private int bitmapWidth;
    private int bitmapHeight;

    //图片当前宽高, 即被缩放后的宽高
    private int currentBitmapWidth;
    private int currentBitmapHeight;

    //view允许的宽高, 即ZoomImageView本身的宽高
    private int layoutWidth;
    private int layoutHeight;

    //缩放时的中心点
    private int centerPointX;
    private int centerPointY;

    //图像文件
    private Bitmap sourceBitmap;
    //2手指之间距离
    private int lastFingerDistance;

    //当前x, y 移动距离
    private int currentTranslateX;
    private int currentTranslateY;

    //最先初始化时的缩放比
    private float initScaleRatio;
    //当前缩放比
    private float currentScaleRatio = 1;
    //在当前缩放比 上继续缩放的比率, 用于当前图片已经是zoom状态, 计算translate的距离
    private float scaleRatio;

    //当前 x, y 和最后一次的距离差值
    private int deltaX;
    private int deltaY;
    private int touchSlop;


    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        currentStatus = STATUS_INIT;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ZoomImageView, defStyle, 0);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();


        a.recycle();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            layoutWidth = getWidth();
            layoutHeight = getHeight();
        }
    }

    private int lastX = -1;
    private int lastY = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "MotionEvent; event.getPointerCount()=" + event.getPointerCount());
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "ACTION_POINTER_DOWN; event.getPointerCount()=" + event.getPointerCount());
                if (event.getPointerCount() == 2){
                    lastFingerDistance = calcuateDistanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1){
                    //移动
                    int x = (int) event.getX();
                    int y = (int) event.getY();

//                Log.d(TAG, "ACTION_MOVE; event.getPointerCount()=" + event.getPointerCount() +"; x=" +x+ "; y=" +y);
                    if ( lastX == -1 && lastY == -1){
                        lastX = x;
                        lastY = y;
                    }

                    if (Math.abs(x-lastX) < touchSlop || Math.abs((y-lastY)) < touchSlop){
                        break;
                    }

                    currentStatus = STATUS_MOVE;
                    deltaX = x - lastX;
                    deltaY = y - lastY;

                    Log.d(TAG, "deltaX=" + deltaX +"; deltaY=" + deltaY + "; touchSlop=" + touchSlop+ "; max=" + (layoutWidth - currentBitmapWidth));

                    currentTranslateX += deltaX;
                    currentTranslateY += deltaY;

                    /*if (currentTranslateX > 0){
                        currentTranslateX = 0;
                    }else if (layoutWidth - currentTranslateX  > currentBitmapWidth  ){
                        currentTranslateX = layoutWidth - currentBitmapWidth;
                    }

                    if (currentTranslateY  > 0){
                        currentTranslateY = 0;
                    }else if (layoutHeight - currentTranslateY  >currentBitmapHeight){
                        currentTranslateY = layoutHeight - currentBitmapHeight;
                    }*/

                    //图片宽小于屏幕宽度
                    if (currentBitmapWidth < layoutWidth){
                        currentTranslateX = (layoutWidth - currentBitmapWidth)/2;
                    }else {
                        if (currentTranslateX > 0){
                            currentTranslateX = 0;
                        }else if (layoutWidth - currentTranslateX > currentBitmapWidth){
                            currentTranslateX = layoutWidth - currentBitmapWidth;
                        }
                    }

                    //图片高度小于布局高
                    if (currentBitmapHeight < layoutHeight){
                        currentTranslateY = (layoutHeight - currentBitmapHeight)/2;
                    }else {
                        if (currentTranslateY > 0){
                            currentTranslateY = 0;
                        }else if (layoutHeight - currentTranslateY > currentBitmapHeight){
                            currentTranslateY = layoutHeight - currentTranslateY;
                        }
                    }

                    invalidate();

                }else if (event.getPointerCount() == 2){
                    //zoom
                    //计算手指的中心点
                    calculateCenterPoint(event);

                    int fingerDistance = calcuateDistanceBetweenFingers(event);
                    if (fingerDistance > lastFingerDistance ){
                        currentStatus = STATUS_ZOOM_OUT;
                    }else if (fingerDistance <= lastFingerDistance){
                        currentStatus = STATUS_ZOOM_IN;
                    }

                    scaleRatio = (float) (fingerDistance*1.0/lastFingerDistance);

                    currentScaleRatio *= scaleRatio;
                    if (currentScaleRatio > 4*initScaleRatio){
                        currentScaleRatio = 4*initScaleRatio;
                    }else if (currentScaleRatio < initScaleRatio){
                        currentScaleRatio = initScaleRatio;
                    }

                    lastFingerDistance = fingerDistance;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2){
                    lastX = lastY = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastX = lastY = -1;
                break;

        }

        return true;
    }

    private void calculateCenterPoint(MotionEvent event) {
        centerPointX = (int) ((event.getX(0) + event.getX(1))/2);
        centerPointY = (int) ((event.getY(0) + event.getY(1))/2);
    }

    private int calcuateDistanceBetweenFingers(MotionEvent event) {
        float disX = event.getX(0) - event.getX(1);
        float disY = event.getY(0) - event.getY(1);
        return (int) Math.sqrt(disX*disX + disY*disY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (currentStatus & STATUS_MASK){
            case STATUS_INIT:
                initBitmap(canvas);
                break;
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoom(canvas);
                break;
            case STATUS_MOVE:
                move(canvas);
                break;
        }
    }

    private void zoom(Canvas canvas) {
        matrix.reset();

        matrix.setScale(currentScaleRatio, currentScaleRatio);

        currentBitmapWidth = (int) (bitmapWidth*currentScaleRatio);
        currentBitmapHeight = (int) (bitmapHeight*currentScaleRatio);

        //图片宽小于屏幕宽度
        if (currentBitmapWidth < layoutWidth){
            currentTranslateX = (layoutWidth - currentBitmapWidth)/2;
        }else {
            currentTranslateX = (int) (currentTranslateX*scaleRatio - centerPointX*scaleRatio + centerPointX);
            if (currentTranslateX > 0){
                currentTranslateX = 0;
            }else if (layoutWidth - currentTranslateX > currentBitmapWidth){
                currentTranslateX = layoutWidth - currentBitmapWidth;
            }
        }

        //图片高度小于布局高
        if (currentBitmapHeight < layoutHeight){
            currentTranslateY = (layoutHeight - currentBitmapHeight)/2;
        }else {
            currentTranslateY = (int) (currentTranslateY*scaleRatio - centerPointY*scaleRatio + centerPointY);
            if (currentTranslateY > 0){
                currentTranslateY = 0;
            }else if (layoutHeight - currentTranslateY > currentBitmapHeight){
                currentTranslateY = layoutHeight - currentTranslateY;
            }
        }


        matrix.postTranslate(currentTranslateX, currentTranslateY);
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void move(Canvas canvas) {
        matrix.reset();
        matrix.setScale(currentScaleRatio, currentScaleRatio);

        matrix.postTranslate(currentTranslateX, currentTranslateY);

        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void initBitmap(Canvas canvas) {
        if (sourceBitmap == null)
            return;

        matrix.reset();
        bitmapHeight = sourceBitmap.getHeight();
        bitmapWidth = sourceBitmap.getWidth();

        if (bitmapWidth > layoutWidth || bitmapHeight > layoutHeight){
            float ratioX = (float) (layoutHeight*1.0/bitmapWidth);
            float ratioY = (float) (layoutHeight*1.0/bitmapHeight);

            if (ratioX < ratioY){
                float scaledY = bitmapHeight*ratioX;

                currentScaleRatio = ratioX;
                currentTranslateY = (int) ((layoutHeight - scaledY)/2);

                matrix.setScale(currentScaleRatio, currentScaleRatio);
                matrix.postTranslate(0f, currentTranslateY);
            }else {
                float scaledX = bitmapWidth * ratioY;

                currentScaleRatio = ratioY;
                currentTranslateX = (int) ((layoutWidth - scaledX)/2);

                matrix.setScale(currentScaleRatio, currentScaleRatio);
                matrix.postTranslate(currentTranslateX, 0);
            }
        }else {
            currentTranslateX = (int) ((layoutWidth - bitmapWidth)*1.0f/2);
            currentTranslateY = (int) ((layoutHeight - bitmapHeight)*1.0f/2);

            matrix.postTranslate(currentTranslateX, currentTranslateY );
        }

        initScaleRatio = currentScaleRatio;
        currentBitmapWidth = (int) (bitmapWidth * currentScaleRatio);
        currentBitmapHeight = (int) (bitmapHeight * currentScaleRatio);

        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    public void setImageBitmap(Bitmap bitmap){
        this.sourceBitmap = bitmap;
//        bitmapWidth = bitmap.getWidth();
//        bitmapHeight = bitmap.getHeight();
        invalidate();
    }
}