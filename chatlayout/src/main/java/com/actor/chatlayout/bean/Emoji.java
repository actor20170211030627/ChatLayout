package com.actor.chatlayout.bean;

import android.graphics.Bitmap;

import com.actor.chatlayout.utils.ConverUtils;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/3 on 14:03
 */
public class Emoji {

    private static final int deaultSize = ConverUtils.dp2px(32);

    public String desc;
    public String filter;//示例: [龇牙]
    public Bitmap icon;
    public String assetsPath;//在assets中的路径, 示例: emoji/[龇牙]@2x.png, Glide加载时: file:/// + assetsPath
    public int    width = deaultSize;
    public int    height = deaultSize;
}
