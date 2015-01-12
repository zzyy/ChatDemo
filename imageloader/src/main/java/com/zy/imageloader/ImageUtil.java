package com.zy.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Simon on 2015/1/12.
 */
public class ImageUtil {

    public static int calcaluteInSampleSize(BitmapFactory.Options options, int reqWidth){
        final int width = options.outWidth;
        int inSample = 1;
        if (width > reqWidth){
            return Math.round((float) width / (float) reqWidth);
        }
        return inSample;
    }

    public static Bitmap decodeSimpleBitmapFromFile(String pathName, int reqWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);
        final int inSampleSize = ImageUtil.calcaluteInSampleSize(options, reqWidth);

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathName, options);
    }

}
