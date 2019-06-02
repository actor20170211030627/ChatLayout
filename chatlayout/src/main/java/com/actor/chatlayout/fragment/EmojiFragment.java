package com.actor.chatlayout.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actor.chatlayout.R;

/**
 * Description: 表情
 * Copyright  : Copyright (c) 2019
 * Date       : 2019/6/2 on 20:08
 */
public class EmojiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emoji, container, false);
    }

}
