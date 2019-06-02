package com.actor.chatlayout;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Description: 事件监听
 * Copyright  : Copyright (c) 2018
 * Author     : actor
 * Date       : 2016/10/4 on 11:40
 */

public abstract class OnListener {

    /**
     * 左侧语音按钮点击事件,一般不用重写此方法监听
     */
    public void onIvVoiceClick(ImageView ivVoice){}

    /**
     * 没有录音等权限
     */
    public void onNoPermission(String permission) {}

    /**
     * 语音录制完成
     * @param audioPath 语音路径, 已判空
     * @param durationMs 语音时长, 单位ms
     */
    public void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs){}

    /**
     * 录音失败
     */
    public void onVoiceRecordError(Exception e) {
    }

    /**
     * 左侧键盘按钮点击事件,一般不用重写此方法监听
     */
    public void onIvKeyBoardClick(ImageView ivKeyBoard) {}

    /**
     * 发送按钮监听,必须重写此方法
     */
    public abstract void onBtnSendClick(EditText etMsg);

    /**
     * 上面部分ListView or RecyclerView的触摸事件,一般不用重写此方法监听
     * @param listView
     * @param event
     */
    public void onListViewTouchListener(View listView, MotionEvent event){}

    /**
     * 布局变化监听,一般不用重写此方法监听
     */
    public void onGlobalLayout(){}

    /**
     * 按住说话的TextView的触摸事件,如果有语音功能,需要重写此方法监听
     */
    public void onTvPressSpeakTouch(TextView tvPressSpeak, MotionEvent event) {}

    /**
     * 右边⊕或ⓧ号点击事件
     */
    public void onIvPlusClick(ImageView ivPlus) {}

    /**
     * 表情的ImageView按钮
     */
    public void onIvEmojiClick(ImageView ivEmoji) {}

    /**
     * EditText的触摸事件,一般不用重写此方法监听
     * @param etMsg
     * @param event
     */
    public void onEditTextToucn(EditText etMsg, MotionEvent event) {}
}
