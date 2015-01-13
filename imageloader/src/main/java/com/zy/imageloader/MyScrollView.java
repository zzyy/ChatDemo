package com.zy.imageloader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Simon on 2015/1/13.
 */
public class MyScrollView  extends ScrollView{

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
}
