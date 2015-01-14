package com.zy.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Simon on 2015/1/13.
 */
public class MyScrollView  extends ScrollView implements ImageLoader.OnLoadedBitmapListener {

    private int columnWidth;

    private int firstColumnHeight;
    private LinearLayout firstColumn;

    private int secondColumnHeight;
    private LinearLayout secondColumn;

    private int thirdColumnHeight;
    private LinearLayout thirdColumn;

    private boolean loadOnce;

    private ImageLoader imageLoader;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        imageLoader = ImageLoader.getInstance();

        firstColumn = (LinearLayout) findViewById(R.id.first_column);
        secondColumn = (LinearLayout) findViewById(R.id.second_column);
        thirdColumn = (LinearLayout) findViewById(R.id.third_column);
        columnWidth = firstColumn.getWidth();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){

        }
    }

    @Override
    public void loadedBitmap(Bitmap bitmap) {
        if (bitmap != null){
            double ratio = bitmap.getWidth()/columnWidth*1.0;
            int scaledHeight = (int) (bitmap.getHeight()/ratio);
            addImage(bitmap, columnWidth, scaledHeight);
        }
    }

    private ImageView mImageView;
    private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
        LinearLayout.LayoutParams params = new ActionMenuView.LayoutParams(imageWidth, imageHeight);

        final ImageView mImageView = new ImageView(getContext());
        mImageView.setLayoutParams(params);
        mImageView.setImageBitmap(bitmap);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setPadding(5, 5, 5, 5);
        mImageView.setTag();


    }
}
