package com.chatlayout.example.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.chatlayout.ChatLayoutKit;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.chatlayout.example.service.CheckUpdateService;

import okhttp3.OkHttpClient;

/**
 * Description: Application
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020-1-6 on 11:08
 *
 * @version 1.0
 */
public class MyApplication extends ActorApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ChatLayoutKit.init(this, true);//Application中初始化
        //初始化语音, 默认最大录音时长2分钟. 如果不用语音, 不用初始化
        AudioUtils.getInstance().init(null, null);

        startService(new Intent(this, CheckUpdateService.class));//检查更新
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder getOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return null;
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
        return "https://api.github.com";
    }

    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace();
    }
}
