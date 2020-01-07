package com.actor.chatlayout.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.chatlayout.ChatLayoutKit;
import com.actor.chatlayout.R;
import com.actor.chatlayout.bean.CustomFaceGroupConfigs;
import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.bean.FaceConfig;
import com.actor.chatlayout.bean.FaceGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaceManager {

    private static final Context                  context          = ChatLayoutKit.getContext();
    private static final List<Emoji>              defaultEmojiList = new ArrayList<>();//默认Emoji列表

//    private static final LruCache<String, Bitmap> drawableCache    = new LruCache<>(1024);
    private static final int                      drawableWidth    = ConverUtils.dp2px(32);
    private static final ArrayList<FaceGroup>     customFace       = new ArrayList<>();

    /**
     * 加载默认Emoji表情
     */
    public static void loadDefaultEmoji() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] emojiFilters = context.getResources().getStringArray(R.array.emoji_filter);
                List<Emoji> emojis = loadEmojisFromAssets(emojiFilters, "emoji");
                if (emojis != null) {
                    defaultEmojiList.clear();
                    defaultEmojiList.addAll(emojis);
                    emojis.clear();
                }
            }
        }).start();
    }

    /**
     * 从assets文件夹中加载Emojis, 加载的Emoji是无序的
     * @param regex 表情的匹配正则, 用于读取表情的输入意思, 示例: [龇牙] 的匹配是: \[\S+]
     * @param assetPath 表情在assets下路径, 示例: emoji(文件夹)
     */
    public static List<Emoji> loadEmojisFromAssets(String regex, String assetPath) {
        try {
            String[] emojis = context.getAssets().list(assetPath);//emoji文件夹下所有表情
            List<Emoji> emojiList = new ArrayList<>(emojis.length);
            Pattern p = Pattern.compile(regex);
            for (String emoji : emojis) {//emoji: [龇牙]@2x.png
                Matcher m = p.matcher(emoji);
                if (m.find()) {
                    String emojiName = m.group();//[龇牙]
                    Emoji emoji1 = new Emoji();
                    emoji1.assetsPath = assetPath + "/" + emoji;
                    emoji1.filter = emojiName;
                    emojiList.add(emoji1);
                }
            }
            return emojiList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从assets文件夹中加载Emojis, 根据 emojiFilters 排序
     * @param emojiFilters emoji表情名称列表, 用于从Assets中读取Emoji后排序, 示例: [龇牙]
     * @param assetPath 表情在assets下路径, 示例: emoji(文件夹)
     */
    public static List<Emoji> loadEmojisFromAssets(String[] emojiFilters, String assetPath) {
        try {
            String[] emojis = context.getAssets().list(assetPath);//emoji文件夹下所有表情
            List<Emoji> emojiList = new ArrayList<>(emojis.length);
            for (String emojiFilter : emojiFilters) {//emojiFilter: [龇牙]
                for (String emoji : emojis) {//emoji: [龇牙]@2x.png
                    if (emoji.contains(emojiFilter)) {
                        Emoji emoji1 = new Emoji();
                        emoji1.assetsPath = assetPath + "/" + emoji;
                        emoji1.filter = emojiFilter;
                        emojiList.add(emoji1);
                    }
                }
            }
            return emojiList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载Emoji
     * @param faceConfigs
     */
    public static void loadFaceFiles(@Nullable final ArrayList<CustomFaceGroupConfigs> faceConfigs) {
        if (faceConfigs == null || faceConfigs.isEmpty()) return;
        new Thread() {
            @Override
            public void run() {
//                ArrayList<CustomFaceGroupConfigs> faceConfigs = TUIKit.getBaseConfigs().getFaceConfigs();//自定义表情配置
                for (int i = 0; i < faceConfigs.size(); i++) {
                    CustomFaceGroupConfigs groupConfigs = faceConfigs.get(i);
                    FaceGroup groupInfo = new FaceGroup();
                    groupInfo.groupId = groupConfigs.faceGroupId;
                    groupInfo.desc = groupConfigs.faceIconName;
                    groupInfo.pageColumnCount = groupConfigs.pageColumnCount;
                    groupInfo.pageRowCount = groupConfigs.pageRowCount;
                    groupInfo.groupIcon = loadAssetBitmap(groupConfigs.faceIconName, groupConfigs.faceIconPath, false).icon;


                    ArrayList<FaceConfig> faceArray = groupConfigs.array;
                    ArrayList<Emoji> faceList = new ArrayList<>();
                    for (int j = 0; j < faceArray.size(); j++) {
                        FaceConfig config = faceArray.get(j);
                        Emoji emoji = loadAssetBitmap(config.faceName, config.assetPath, false);
                        emoji.width = config.faceWidth;
                        emoji.height = config.faceHeight;
                        faceList.add(emoji);

                    }
                    groupInfo.faces = faceList;
                    customFace.add(groupInfo);
                }
            }
        }.start();
    }

    /**
     * 从assets文件夹中加载Emoji
     * @param filter 表情的filter, 示例: [龇牙]
     * @param assetPath 表情在assets下路径, 示例: emoji/[龇牙]@2x.png
     * @param isEmoji 是否是emoji, 示例: true
     * @return 自定义表情
     */
    private static Emoji loadAssetBitmap(String filter, String assetPath, boolean isEmoji) {
        InputStream is = null;
        try {
            Emoji emoji = new Emoji();
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_XXHIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(0, 0, drawableWidth, drawableWidth), options);
            if (bitmap != null) {
//                drawableCache.put(filter, bitmap);
                emoji.icon = bitmap;
                emoji.filter = filter;
                if (isEmoji) defaultEmojiList.add(emoji);
            }
            return emoji;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 给TextView 设置有emoji 的文字
     * @param content
     */
    public static void handlerEmojiText(TextView textView, CharSequence content) {
        if(textView == null) return;
        if (TextUtils.isEmpty(content)) {
            textView.setText(content);
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();//[龇牙]
            for (final Emoji emoji : defaultEmojiList) {
                if (tempText.equals(emoji.filter)) {
                    //Bitmap bitmap = drawableCache.get(tempText);
                    try {
                        Bitmap bitmap = assets2Bitmap(context, emoji.assetsPath);
                        //转换为Span, SPAN_INCLUSIVE_EXCLUSIVE: 2个Span之间不能输入文字...
                        ssb.setSpan(new ImageSpan(context, bitmap), m.start(), m.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
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
    public static Bitmap assets2Bitmap(Context context, String assetsPath){
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
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
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

    public static List<Emoji> getDefaultEmojiList() {
        return defaultEmojiList;
    }

    public static List<FaceGroup> getCustomFaceList() {
        return customFace;
    }

    public static Bitmap getCustomBitmap(int groupId, String name) {
        for (int i = 0; i < customFace.size(); i++) {
            FaceGroup group = customFace.get(i);
            if (group.groupId == groupId) {
                ArrayList<Emoji> faces = group.faces;
                for (int j = 0; j < faces.size(); j++) {
                    Emoji face = faces.get(j);
                    if (face.filter.equals(name)) {
                        return face.icon;
                    }
                }

            }
        }
        return null;
    }

    public static boolean isFaceChar(String faceChar) {
//        return drawableCache.get(faceChar) != null;
        return false;
    }
}
