package com.actor.chatlayout;

import android.app.Application;
import android.content.Context;

import com.actor.chatlayout.utils.FaceManager;

/**
 * Description: 用作初始化, 项目地址: https://github.com/actor20170211030627/ChatLayout
 * Author     : 李大发
 * Date       : 2019/6/3 on 20:45
 */
public class ChatLayoutKit {

    private static Application application;

    /**
     * @param application 传入Application
     * @param loadDefaultEmoji 是否加载默认Emoji
     */
    public static void init(Application application, boolean loadDefaultEmoji) {
        ChatLayoutKit.application = application;
        if (loadDefaultEmoji) FaceManager.loadDefaultEmoji();
    }

    public static Context getContext() {
        return application;
    }
}
