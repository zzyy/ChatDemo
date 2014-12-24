package com.zy.chat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zy.chat.R;

/**
 * TODO: document your custom view class.
 */
public class ChangeColorButton extends View {
    private int mColor = Color.RED; // TODO: use a default from R.color...
    private float mTextSize = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Drawable mIcon;
    private String mText;
    private Rect mIconRect;

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

        mIcon = a.getDrawable(R.styleable.ChangeColorButton_icon);
        mColor = a.getColor(R.styleable.ChangeColorButton_color, mColor);
        mText = a.getString(R.styleable.ChangeColorButton_text);
        mTextSize = a.getDimension(R.styleable.ChangeColorButton_text_size, mTextSize);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
//        mTextPaint.setTextSize(mTextSize);
//        mTextPaint.setColor(mColor);

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        mTextWidth = mTextPaint.measureText(mText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
        Log.d("zy", "fontMetrics=" + fontMetrics + "; fontMetrics.bottom=" + fontMetrics.bottom +
                "; fontMetrics.top="+ fontMetrics.top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int bitmapWidth = (int) Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - mTextHeight);
        int left = (getMeasuredWidth() - bitmapWidth)/2;
        int top = (getMeasuredHeight() - bitmapWidth)/2;

        mIconRect = new Rect(left, top, left+bitmapWidth, top+bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

}
