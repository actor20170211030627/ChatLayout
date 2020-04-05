package com.actor.chatlayout.bean;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * description: Emoji表情实体类
 * author     : 李大发
 * date       : 2019/6/3 on 14:03
 */
public class Emoji {

    public static final int DEAULT_SIZE = ConvertUtils.dp2px(25);

    //如果Emoji来自assets, Glide加载时, 需要在前面加上这个前缀
    public static final String ASSETS_PREFIX = "file:///android_asset/";

    public int    groupId;//表情所在组id

    public String filter;//示例: "[龇牙]"

    //Emoji是否来自assets
//    public boolean isEmojiFromAssets;

    //在assets中的路径, 示例: "emoji/[龇牙]@2x.png", Glide加载时: Emoji.ASSETS_PREFIX + assetsPath
    public @Nullable String assetsPath;

    //drawable or raw 资源id
    public @Nullable Integer drawable$RawId;

    public int    width;
    public int    height;

    public Emoji(String filter, String assetsPath, int width, int height) {
        this.filter = filter;
        this.assetsPath = assetsPath;
        this.width = width;
        this.height = height;
    }

    public Emoji(String filter, Integer drawable$RawId, int width, int height) {
        this.filter = filter;
        this.drawable$RawId = drawable$RawId;
        this.width = width;
        this.height = height;
    }
}
