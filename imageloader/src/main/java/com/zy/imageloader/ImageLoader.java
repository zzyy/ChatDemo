package com.zy.imageloader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Simon on 2015/1/12.
 */
public class ImageLoader {

    private static LruCache<String, Bitmap> mImageMemoryCache;

    private static ImageLoader mInstance;
    public ImageLoader getInstance(){
        if (mInstance == null){
            mInstance = new ImageLoader();
        }
        return mInstance;
    }
    private ImageLoader(){
        int maxMermory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMermory/8;

        mImageMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void addBitmapToMemoery(String key, Bitmap value){
        if (getBitmapFromMemory(key) == null){
            mImageMemoryCache.put(key, value);
        }
    }

    public Bitmap getBitmapFromMemory(String key){
        return mImageMemoryCache.get(key);
    }

}
