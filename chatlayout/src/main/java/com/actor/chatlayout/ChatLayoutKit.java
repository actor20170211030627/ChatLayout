package com.actor.chatlayout;

import android.app.Application;
import android.content.Context;

import com.actor.chatlayout.utils.FaceManager;

/**
 * Description: 用作初始化
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/3 on 20:45
 */
public class ChatLayoutKit {

    private static Application application;

    /**
     * 传入ApplicationContext
     */
    public static void init(Application application) {
        ChatLayoutKit.application = application;
        FaceManager.loadFaceFiles(null);
    }

    public static Context getContext() {
        return application;
    }
}
