package com.chatlayout.example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chatlayout.utils.DefaultEmojiList;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.actor.myandroidframework.widget.chat.ChatLayoutKit;

import okhttp3.OkHttpClient;

/**
 * Description: Application
 * Company    :
 * Author     : ldf
 * Date       : 2020-1-6 on 11:08
 *
 * @version 1.0
 */
public class MyApplication extends ActorApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //如果需要使用emoji表情, 需要在Application中初始化
        ChatLayoutKit.init(DefaultEmojiList.DEFAULT_EMOJI_LIST, "emoji");
        //初始化语音, 默认最大录音时长2分钟. 如果不用语音, 不用初始化
        AudioUtils.getInstance().init(null, null);
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return null;
    }

    @NonNull
    @Override
    protected String getBaseUrl(boolean isDebugMode) {
        return "https://api.github.com";
    }

    @Override
    protected void onUncaughtException(Throwable e) {
    }
}
