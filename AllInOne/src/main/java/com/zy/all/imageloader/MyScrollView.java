package com.zy.all.imageloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;

import com.zy.all.R;
import com.zy.all.imageloader.activity.ShowImageActivity;

/**
 * Created by Simon on 2015/1/13.
 */
public class MyScrollView  extends ScrollView implements ImageLoader.OnLoadedBitmapListener {
    private static final String TAG ="zy";

    private int page;
    private static final int PAGE_SIZE = 15;

    //每列的宽
    private int columnWidth;

    //每列的高度
    private int firstColumnHeight;
    private LinearLayout firstColumn;

    private int secondColumnHeight;
    private LinearLayout secondColumn;

    private int thirdColumnHeight;
    private LinearLayout thirdColumn;

    private static View scrollLayout;
    private static int scrollViewHeight;

    private boolean loadOnce;

    //用于存储所有的imageView
    private ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();

    private ImageLoader imageLoader;

    public static int lastScrollY;

/*    private static Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            MyScrollView myScrollView = (MyScrollView) msg.obj;
            int scrollY = myScrollView.getScrollY();

            if (scrollY == lastScrollY){
                if (scrollViewHeight + scrollY >= scrollLayout.getHeight() && myScrollView.imageLoader.isExistTask()){
                    myScrollView.loadMoreImages();
                }

                myScrollView.checkVisibility();

            }else {
                lastScrollY = scrollY;
                Message message = Message.obtain();
                message.obj = myScrollView;
                handler.sendMessageDelayed(message, 5);
            }

        }
    };*/

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageLoader = ImageLoader.getInstance();
        imageLoader.setOnLoadedBitmapListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP){
            /*Message msg = Message.obtain();
            msg.obj = this;
            handler.sendMessageDelayed(msg, 10) ;*/
        }

        return super.onTouchEvent(ev);
    }


    private void checkVisibility() {
        int N = imageViewList.size();
        for (int i=0; i<N; i++){
            ImageView imageView = imageViewList.get(i);

            int borderTop = (int) imageView.getTag(R.string.border_top);
            int borderBottom = (int) imageView.getTag(R.string.border_bottom);

            if (borderBottom > getScrollY()
                    && borderTop < getScrollY() + scrollViewHeight) {
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                else {
                    imageLoader.loadBitmap(imageUrl, imageView);
                }
            } else {
                imageView.setImageResource(R.drawable.empty_photo);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce){

            scrollViewHeight = getHeight();

            firstColumn = (LinearLayout) findViewById(R.id.first_column);
            secondColumn = (LinearLayout) findViewById(R.id.second_column);
            thirdColumn = (LinearLayout) findViewById(R.id.third_column);
            columnWidth = firstColumn.getWidth();

            scrollLayout = getChildAt(0);

            Log.d(TAG, "scrollViewHeight=" + scrollViewHeight +"; scrollLayout="+ scrollLayout);
            imageLoader.setImageWidth(columnWidth);

            loadMoreImages();

            loadOnce = true;
        }
    }

    private String[] imageUrls = UrlString.imageUrls;

    private void loadMoreImages() {

        int startIndex = page*PAGE_SIZE;
        int endIndex = page*PAGE_SIZE + PAGE_SIZE;

        final int maxImageIndex = imageUrls.length;
        if (startIndex < maxImageIndex) {

            if (endIndex > imageUrls.length) {
                endIndex = imageUrls.length;
            }

            for (int i = startIndex; i < endIndex; i++) {
                imageLoader.loadBitmap(imageUrls[i]);
            }
            page++;
        } else {
            Toast.makeText(getContext(), "没有更多图片 \n 随机加载", Toast.LENGTH_SHORT).show();
            final Random random = new Random();
            for (int i=0; i<15; i++){
                imageLoader.loadBitmap(imageUrls[random.nextInt(maxImageIndex)]);
            }
        }

    }

    //加载图片后 回调
    @Override
    public void loadedBitmap(String url, View view, Bitmap bitmap) {
//        Log.d(TAG, "loadedBitmap; bitmap=" + bitmap);
        if (bitmap == null) return;

        final ImageView imageView = (ImageView) view;

        if (imageView != null){
            imageView.setImageBitmap(bitmap);
        }else {
            double ratio = bitmap.getWidth() / (columnWidth * 1.0);
            int scaledHeight = (int) (bitmap.getHeight() / ratio);
            Log.d(TAG, "ratio=" + ratio + "; bitmap.getWidth()=" + bitmap.getWidth() + "; bitmap.getHeight()=" + bitmap.getHeight());
            addImage(url, bitmap, columnWidth, scaledHeight);
        }
    }

    private void addImage(final String url, Bitmap bitmap, int imageWidth, int imageHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);

        final ImageView mImageView = new ImageView(getContext());
        mImageView.setLayoutParams(params);
        mImageView.setImageBitmap(bitmap);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setPadding(5, 5, 5, 5);
        mImageView.setTag(R.string.image_url, url);

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowImageActivity.class);
                intent.putExtra("imageUrl", url);
                getContext().startActivity(intent);
            }
        });

        Log.d(TAG, "imageHeight="+ imageHeight +"; imageWidth="+ imageWidth +"; image_url=" + url);
        findColumnToAdd(mImageView, imageHeight).addView(mImageView);
        imageViewList.add(mImageView);
    }

    /**
     * 找到哪一列该增加图片
     * @param imageView
     * @param imageHeight
     * @return
     */
    private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
        if (firstColumnHeight <= secondColumnHeight) {
            if (firstColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, firstColumnHeight);
                firstColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, firstColumnHeight);
                return firstColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        } else {
            if (secondColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, secondColumnHeight);
                secondColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, secondColumnHeight);
                return secondColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        }
    }

}
