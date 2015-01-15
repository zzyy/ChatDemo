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
import android.view.MotionEvent;
import android.view.View;

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

    //view允许的宽高, 即ZoomImageView本身的宽高
    private int layoutWidth;
    private int layoutHeight;

    //图像文件
    private Bitmap sourceBitmap;




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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (currentStatus & STATUS_MASK){
            case STATUS_INIT:
                initBitmap(canvas);
                break;
            case STATUS_ZOOM_OUT:
                break;
            case STATUS_ZOOM_IN:
                break;
            case STATUS_MOVE:
                break;
        }
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
                matrix.setScale(ratioX, ratioX);

                float scaledY = bitmapHeight*ratioX;
                matrix.postTranslate(0f, (layoutHeight - scaledY)/2);
            }else {
                matrix.setScale(ratioY, ratioY);

                float scaledX = bitmapWidth * ratioY;
                matrix.postTranslate((layoutWidth - scaledX)/2, 0);
            }
        }else {
            matrix.postTranslate((layoutWidth - bitmapWidth)*1.0f/2, (layoutHeight - bitmapHeight)*1.0f/2 );
        }

        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    public void setImageBitmap(Bitmap bitmap){
        this.sourceBitmap = bitmap;
//        bitmapWidth = bitmap.getWidth();
//        bitmapHeight = bitmap.getHeight();
        invalidate();
    }
}