package com.zy.chat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zy.chat.R;

/**
 * TODO: document your custom view class.
 */
public class ChangeColorButton extends View {
    private int mColor = Color.BLUE;
    private float mTextSize = 12;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private String mText;
    private Rect mIconRect;
    private Bitmap mIconBitmap;
    private float mAlpha;
    private Bitmap mBitmap;

    public ChangeColorButton(Context context) {
        super(context);
        init(null, 0);
    }

    public ChangeColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChangeColorButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ChangeColorButton, defStyle, 0);

        BitmapDrawable mIconDrawable = (BitmapDrawable) a.getDrawable(R.styleable.ChangeColorButton_icon);
        mIconBitmap = mIconDrawable.getBitmap();

        mColor = a.getColor(R.styleable.ChangeColorButton_color, mColor);
        mText = a.getString(R.styleable.ChangeColorButton_text);
        mTextSize = a.getDimension(R.styleable.ChangeColorButton_text_size, mTextSize);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
//        mTextPaint.setTextSize(mTextSize);
//        mTextPaint.setColor(mColor);

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        mTextWidth = mTextPaint.measureText(mText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = (float) Math.ceil(fontMetrics.bottom - fontMetrics.ascent);
        Log.d("zy", "mTextWidth=" +mTextWidth +"; mTextHeight="+ mTextHeight);
        Log.d("zy", "fontMetrics=" + fontMetrics + "; fontMetrics.bottom=" + fontMetrics.bottom +
                "; fontMetrics.top=" + fontMetrics.top);

        Rect mTextBound = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        Log.d("zy", String.format("getTextBounds mTextWidth %d, mTextHeight %d", mTextBound.width(), mTextBound.height()));

//        mTextWidth = mTextBound.width();
//        mTextHeight = mTextBound.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom() - (int)mTextHeight);
        int left = (getMeasuredWidth() - bitmapWidth) / 2;
        int top = (int) ((getMeasuredHeight() - mTextHeight) / 2 - bitmapWidth/ 2);

        Log.d("zy", "");

        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
        Log.d("zy", String.format("bitmapWidth=%d; getMeasuredHeight=%d; getMeasuredWidth=%d" , bitmapWidth, getMeasuredHeight(), getMeasuredHeight()));
        Log.d("zy" , "mIconRect=" + mIconRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int alpha = (int) Math.ceil(255*mAlpha);
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        setupTargetBitmap(alpha);

//        canvas.drawText(mText, mIconRect.left, mIconRect.bottom,
//               mIconRect.right, mIconRect.bottom + mTextHeight, mTextPaint);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas ,alpha);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + (mIconRect.width() - mTextWidth)/2 ,
                mIconRect.bottom + mTextHeight, mTextPaint);

    }

    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        Paint mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha){
        mTextPaint.setAlpha(255 - alpha);
        mTextPaint.setColor(0xff333333);
        canvas.drawText(mText, mIconRect.left + (mIconRect.width() - mTextWidth)/2 ,
                mIconRect.bottom + mTextHeight, mTextPaint);
    }

    public void setIconAlpha (float alpha){
        mAlpha = alpha;
        invalidateView();
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        }else {
            postInvalidate();
        }
    }
}
