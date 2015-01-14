package com.zy.imageloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

/**
 * Created by Simon on 2015/1/12.
 */
public class ImageLoader {
    private static final String TAG = "zy";
    private Context mContext;

    private static LruCache<String, Bitmap> mImageMemoryCache;
    private static DiskLruCache mDiskLruCache;

    private static ImageLoader mInstance;

    private HashSet<BitmapWorkerTask> taskCollection;

    private OnLoadedBitmapListener mOnLoadedBitmapListener;
    private int mImageWidth;

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            mInstance = new ImageLoader();
        }
        return mInstance;
    }

    private ImageLoader() {
        int maxMermory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMermory / 8;

        //初始化 LruCache
        mImageMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        mContext = AppApplication.getContext();

        mImageWidth = getScreenWidth(mContext)/3;

        try {
            File cacheDir = getDiskCacheDir(mContext, "thunbnail");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, 30 * 1024 * 1024);

        } catch (IOException e) {
            e.printStackTrace();
        }

        taskCollection = new HashSet<>();
    }

    /**
     * 获取屏幕宽度, 用于计算图片宽度的默认值
     * @param context
     * @return
     */
    public int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setImageWidth(int mImageWidth) {
        this.mImageWidth = mImageWidth;
    }

    private int getAppVersion(Context mContext) {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private File getDiskCacheDir(Context mContext, String path) {
        File cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
                !Environment.isExternalStorageRemovable()) {
            cachePath = mContext.getExternalCacheDir();
        } else {
            cachePath = mContext.getCacheDir();
        }

        Log.d(TAG, "diskCacheDir=" + cachePath + File.separator + path);
        return new File(cachePath + File.separator + path);
    }

    public void addBitmapToMemoery(String key, Bitmap value) {
        if (getBitmapFromMemory(key) == null) {
            mImageMemoryCache.put(key, value);
        }
    }

    public Bitmap getBitmapFromMemory(String key) {
        return mImageMemoryCache.get(key);
    }

    public void loadBitmap(String url) {
        Bitmap bitmap = getBitmapFromMemory(url);
        if (bitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask();
            taskCollection.add(task);
            task.execute(url);
        } else {
            callOnLoadedBitmap(bitmap);
        }
    }

    public void cancleAllTasks(){
        if (taskCollection != null){
            for (BitmapWorkerTask task : taskCollection){
                task.cancel(false);
            }
        }
    }

    public boolean isExistTask(){
        return taskCollection.isEmpty();
    }

    /**
     * 将缓存记录同步到 journal 文件中去
     */
    public void flushCache(){
        if (mDiskLruCache != null){
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;

            try {
                final String key = ImageUtil.hashKeyForDisk(imageUrl);
                snapshot = mDiskLruCache.get(key);

                if (snapshot == null) {
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    OutputStream outputStream = editor.newOutputStream(0);
                    final boolean isDownloadSuccess = downloadUrlToStream(imageUrl, outputStream);
                    if ( isDownloadSuccess) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                    flushCache();
Log.d(TAG, "isDownloadSuccess=" + isDownloadSuccess);

                    snapshot = mDiskLruCache.get(key);
                }

                if (snapshot != null){
//                    Log.d(TAG, "try to get inputStream from snapshot");
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }

                //解析为 bitmap
                Bitmap bitmap = null;
                if (fileDescriptor != null){
//                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                   bitmap = ImageUtil.decodeSimpleBitmapFromStream(fileInputStream, mImageWidth);
                    bitmap = ImageUtil.decodeSimpleBitmapFromFileDescriptor(fileDescriptor, mImageWidth);
                }

                //添加到内存缓存中间
                if (bitmap != null){
                    addBitmapToMemoery(imageUrl, bitmap);
                }

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 创建httpConnection 对象, 下载 并写入到 outputStream
         * @param imageUrl
         * @param outputStream
         * @return
         */
        private boolean downloadUrlToStream(String imageUrl, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bin = null;

            try {
                final URL url = new URL(imageUrl);
                urlConnection = (HttpURLConnection) url.openConnection();

                bin = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                bos = new BufferedOutputStream(outputStream, 8 * 1024);

                int b;
                while ((b = bin.read()) != -1) {
                    bos.write(b);
                }
                bos.flush();
Log.d(TAG, "image download finished");

                return true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (bin != null) {
                        bin.close();
                        bin = null;
                    }
                    if (bos != null) {
                        bos.close();
                        bos = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            taskCollection.remove(this);

            callOnLoadedBitmap(bitmap);
        }
    }


    void callOnLoadedBitmap(Bitmap bitmap) {
        if (mOnLoadedBitmapListener != null) {
            mOnLoadedBitmapListener.loadedBitmap(bitmap);
        }
    }

    public void setOnLoadedBitmapListener(OnLoadedBitmapListener l) {
        mOnLoadedBitmapListener = l;
    }

    public interface OnLoadedBitmapListener {
        void loadedBitmap(Bitmap bitmap);
    }
}

