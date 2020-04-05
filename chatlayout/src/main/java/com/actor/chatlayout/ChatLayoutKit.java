package com.actor.chatlayout;

import android.app.Application;
import android.graphics.Bitmap;

import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.utils.DefaultEmojiList;
import com.actor.chatlayout.utils.FaceManager;
import com.blankj.utilcode.util.ImageUtils;

import java.util.Arrays;
import java.util.List;

/**
 * description: 用作初始化, 项目地址: https://github.com/actor20170211030627/ChatLayout
 * author     : 李大发
 * date       : 2019/6/3 on 20:45
 * @version 1.0
 */
public class ChatLayoutKit {

    public static Application context;

    /**
     * @param application 传入Application
     * @param loadDefaultEmoji 是否加载默认Emoji, 如果false, 你可以自定义加载自己的表情.
     */
    public static void init(Application application, boolean loadDefaultEmoji) {
        context = application;
        if (loadDefaultEmoji) {
            List<String> emojiNames = Arrays.asList(DefaultEmojiList.defaultEmojiList);
            FaceManager.loadEmojisFromAssets(emojiNames, "emoji", Emoji.DEAULT_SIZE, Emoji.DEAULT_SIZE,  new FaceManager.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(List<Emoji> emojis) {
                    FaceManager.setEmojiList(emojis, FaceManager.DEFAULT_EMOJI_REGEX);

                    //设置默认Drawable, 用于显示在TabLayout下方
                    String assetsPath = emojis.get(0).assetsPath;
                    Bitmap bitmap = FaceManager.assets2Bitmap(assetsPath);
                    FaceManager.emojiDrawableShowInTabLayout = ImageUtils.bitmap2Drawable(bitmap);
                }
            });
        }
    }
}
