package com.actor.chatlayout.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.chatlayout.ChatLayoutKit;
import com.actor.chatlayout.bean.Emoji;
import com.actor.myandroidframework.utils.ThreadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: 加载表情等
 * author     : 李大发
 * date       : 2019/6/3 on 22:16
 * @version 1.0
 */
public class FaceManager {

    protected static final Context     context             = ChatLayoutKit.context;
    protected static final List<Emoji> emojiList           = new ArrayList<>();//Emoji列表

    /**
     * 默认表情的正则:
     * \[    转义左中括号
     * \S    匹配所有非空白字符
     * ?     标记?之前的字符为"可选". 0 或 1 次
     */
    public static final    String      DEFAULT_EMOJI_REGEX = "\\[\\S+?]";
    public static          String      EMOJI_REGEX;//你加载的Emoji的正则

    /**
     * 从assets文件夹中加载Emojis, 加载的Emoji是无序的
     * @param regex 表情的匹配正则, 用于读取表情的输入意思, 示例: "[龇牙]" 的匹配是 {@link #DEFAULT_EMOJI_REGEX}
     * @param assetPathName 表情在assets下路径, 示例: "emoji"(表情在这个文件夹内)
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(String regex, String assetPathName, OnLoadCompleteListener listener) {
        if (listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] emojis = context.getAssets().list(assetPathName);//emoji文件夹下所有表情
                    if (emojis == null || emojis.length == 0) {
                        listener.onLoadComplete(null);
                    } else {
                        List<Emoji> emojiList = new ArrayList<>(emojis.length);
                        Pattern p = Pattern.compile(regex);
                        for (String emoji : emojis) {//emoji图片表情名称: "[龇牙]@2x.png"
                            Matcher m = p.matcher(emoji);
                            if (m.find()) {
                                String emojiName = m.group();//匹配到的第一组 "[龇牙]"
                                //"[龇牙]", "emoji/[龇牙]@2x.png"
                                Emoji emoji1 = new Emoji(emojiName, assetPathName + "/" + emoji);
                                emojiList.add(emoji1);
                            }
                        }
                        listener.onLoadComplete(emojiList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onLoadComplete(null);
                }
            }
        });
    }

    /**
     * 从assets文件夹中加载Emojis, 根据 emojiFilters 排序
     * @param emojiNames emoji表情名称列表, 用于从Assets中读取Emoji后排序, 示例: "[龇牙]"
     * @param assetPathName 表情在assets下路径, 示例: "emoji"(表情在这个文件夹内)
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(List<String> emojiNames, String assetPathName, OnLoadCompleteListener listener) {
        if (listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] emojis = context.getAssets().list(assetPathName);//emoji文件夹下所有表情
                    if (emojis == null || emojis.length == 0) {
                        listener.onLoadComplete(null);
                    } else {
                        List<Emoji> emojiList = new ArrayList<>(emojis.length);
                        for (String emojiName : emojiNames) {//emojiName: "[龇牙]"
                            for (String emoji : emojis) {//emoji图片表情名称: "[龇牙]@2x.png"
                                if (emoji.contains(emojiName)) {
                                    //"[龇牙]", "emoji/[龇牙]@2x.png"
                                    Emoji emoji1 = new Emoji(emojiName, assetPathName + "/" + emoji);
                                    emojiList.add(emoji1);
                                }
                            }
                        }
                        listener.onLoadComplete(emojiList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onLoadComplete(null);
                }
            }
        });
    }

    public interface OnLoadCompleteListener {
        /**
         * 加载完成
         */
        void onLoadComplete(List<Emoji> emojis);
    }

    /**
     * 给TextView 设置有 emoji 的文字
     * @param regex 用于匹配emoji的正则, 例: {@link #DEFAULT_EMOJI_REGEX}
     * @param content 包含emoji的内容
     */
    public static void handlerEmojiText(TextView textView, String regex, CharSequence content) {
        if(textView == null) return;
        if (TextUtils.isEmpty(content)) {
            textView.setText(content);
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();//[龇牙]
            for (Emoji emoji : emojiList) {
                if (tempText.equals(emoji.filter)) {
                    Bitmap bitmap = null;
                    if (emoji.assetsPath != null) {//assets
                        bitmap = assets2Bitmap(emoji.assetsPath);
                    } else if (emoji.drawable$RawId != null) {//drawable / raw
                        bitmap = BitmapFactory.decodeResource(context.getResources(), emoji.drawable$RawId);
                    }
                    if (bitmap != null) {
                        //转换为Span, SPAN_INCLUSIVE_EXCLUSIVE: 2个Span之间不能输入文字...
                        ssb.setSpan(new ImageSpan(context, bitmap), m.start(), m.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                }
            }
        }
        int selection = textView.getSelectionStart();
        textView.setText(ssb);
        if (textView instanceof EditText) {
            ((EditText) textView).setSelection(selection);
        }
    }

    /** Assets转Bitmap
     * @param assetsPath 在assets中的路径, 示例: emoji/[龇牙]@2x.png
     */
    public static Bitmap assets2Bitmap(String assetsPath) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(assetsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public static Bitmap decodeSampledBitmapFromResource(int resId, int reqWidth, int reqHeight) {
        Resources res = context.getResources();
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 如果你加载了默认表情, 返回默认加载的表情列表
     */
    public static List<Emoji> getEmojiList() {
        return emojiList;
    }

    /**
     * 设置表情列表
     */
    public static void setEmojiList(List<Emoji> list, boolean defaultEmoji, String regex) {
        if (list != null) {
            if (defaultEmoji) {
                emojiList.clear();
                emojiList.addAll(list);
            } else {
                // TODO: 2020/4/4
            }
            EMOJI_REGEX = regex;
        }
    }

//    public static List<FaceGroup> getCustomFaceList() {
//        return customFace;
//    }
//
//    public static Bitmap getCustomBitmap(int groupId, String name) {
//        for (int i = 0; i < customFace.size(); i++) {
//            FaceGroup group = customFace.get(i);
//            if (group.groupId == groupId) {
//                ArrayList<Emoji> faces = group.faces;
//                for (int j = 0; j < faces.size(); j++) {
//                    Emoji face = faces.get(j);
//                    if (face.filter.equals(name)) {
//                        return face.icon;
//                    }
//                }
//            }
//        }
//        return null;
//    }
}
