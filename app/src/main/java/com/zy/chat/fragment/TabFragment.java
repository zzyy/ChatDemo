package com.zy.chat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zy.chat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {


    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        TextView textView = (TextView) view.findViewById(R.id.content);
        String mTitle = getArguments().getString("title");
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTitle);
        return view;
    }


}
