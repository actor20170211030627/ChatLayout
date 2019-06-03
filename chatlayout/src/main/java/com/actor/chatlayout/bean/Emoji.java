package com.actor.chatlayout.bean;

import android.graphics.Bitmap;

import com.actor.chatlayout.utils.ConverUtils;

/**
 * Description: 类的描述
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/3 on 14:03
 */
public class Emoji {

    private static final int deaultSize = ConverUtils.dp2px(32);

    public String desc;
    public String filter;
    public Bitmap icon;
    public int    width = deaultSize;
    public int    height = deaultSize;
}
