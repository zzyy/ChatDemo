package com.zy.all.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static Bitmap decodeSimpleBitmapFromStream(InputStream inputStream, int reqWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream, null, options);
        final int inSampleSize = ImageUtil.calcaluteInSampleSize(options, reqWidth);

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static Bitmap decodeSimpleBitmapFromFileDescriptor(FileDescriptor fileDescriptor, int reqWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        final int inSampleSize = ImageUtil.calcaluteInSampleSize(options, reqWidth);

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    public static String hashKeyForDisk(String key){
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update( key.getBytes() );
            cacheKey = bytesToHexString( mDigest.digest() );
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf( key.hashCode() );
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        int N =bytes.length;
        for (int i = 0; i<N; i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
