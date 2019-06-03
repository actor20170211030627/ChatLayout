package com.actor.chatlayout;

import android.content.Context;

import com.actor.chatlayout.utils.FaceManager;

/**
 * Description: 用作初始化
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/3 on 20:45
 */
public class ChatLayoutKit {

    private static Context context;

    /**
     * 传入ApplicationContext
     */
    public static void init(Context context) {
        ChatLayoutKit.context = context;
        FaceManager.loadFaceFiles(null);
    }

    public static Context getContext() {
        return context;
    }
}
