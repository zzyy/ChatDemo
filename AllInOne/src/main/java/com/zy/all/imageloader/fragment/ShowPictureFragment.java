package com.zy.all.imageloader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zy.all.R;

import com.zy.all.imageloader.ImageLoader;
import com.zy.all.imageloader.ImageUtil;
import com.zy.all.imageloader.ui.ZoomImageView;

/**
 * Created by Simon on 2015/1/15.
 */
public class ShowPictureFragment extends Fragment  {
    private static final String TAG = "zy.ShowPictureFragment";

    private ZoomImageView mZoomImageView;
    private ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_show_picture, container);
        mZoomImageView = (ZoomImageView) view.findViewById(R.id.image_content);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Bundle args = getArguments();
//        final String imageUrl = args.getString("imageUrl");

        Intent intent = getActivity().getIntent();
        final String imageUrl = intent.getStringExtra("imageUrl");
        Log.d(TAG, "imageUrl=" + imageUrl);
        final String key = ImageUtil.hashKeyForDisk(imageUrl);
        mZoomImageView.setImageBitmap(imageLoader.getBitmapFromDiskCache(key));
    }

}
