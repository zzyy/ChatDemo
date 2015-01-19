package com.zy.all.imageloader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zy.all.R;
import com.zy.all.imageloader.MyScrollView;


/**
 * Created by Simon on 2015/1/19.
 */
public class AllPicturesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_picture, container);
    }
}
