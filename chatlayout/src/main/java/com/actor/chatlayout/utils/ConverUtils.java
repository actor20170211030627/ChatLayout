package com.actor.chatlayout.utils;

import com.actor.chatlayout.ChatLayoutKit;

/**
 * Description: 转换类
 * Copyright  : Copyright (c) 2019
 * Author     : actor
 * Date       : 2019/6/3 on 13:29
 */
public class ConverUtils {

    public static int dp2px(float dipValue) {
        final float scale = ChatLayoutKit.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
