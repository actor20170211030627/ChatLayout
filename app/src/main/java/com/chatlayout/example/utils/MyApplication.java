package com.chatlayout.example.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chatlayout.ChatLayoutKit;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.audio.AudioUtils;

import okhttp3.OkHttpClient;

/**
 * Description: Application
 * Author     : 李大发
 * Date       : 2020-1-6 on 11:08
 *
 * @version 1.0
 */
public class MyApplication extends ActorApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //Application中初始化
        ChatLayoutKit.init(this, true);
        //初始化语音, 默认最大录音时长2分钟. 如果不用语音, 不用初始化
        AudioUtils.getInstance().init(null, null);
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return builder;
    }

    @NonNull
    @Override
    protected String getBaseUrl(boolean isDebugMode) {
        return Global.BASE_URL;
    }

    @Override
    protected void onUncaughtException(Throwable e) {
        e.printStackTrace();
        System.exit(-1);
    }
}
