package com.actor.chatlayout.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.actor.chatlayout.ChatLayoutKit;

/**
 * Description: 键盘
 * Author     : 李大发
 * Date       : 2019/6/3 on 20:22
 */
public class KeyboardUtils {

    private static final String KEYBOARD_HEIGHT = "KEYBOARD_HEIGHT_KEYBOARD_HEIGHT";//键盘高度
    public static int getKeyboardHeight(){
        SharedPreferences sp = ChatLayoutKit.getContext().getSharedPreferences(KEYBOARD_HEIGHT, Context.MODE_PRIVATE);
        return sp.getInt(KEYBOARD_HEIGHT, 831);//手写:478 语音:477 26键:831.screenHeight / 5
    }

    public static void saveKeyboardHeight(int value){
        SharedPreferences sp = ChatLayoutKit.getContext().getSharedPreferences(KEYBOARD_HEIGHT, Context.MODE_PRIVATE);
        sp.edit().putInt(KEYBOARD_HEIGHT, value).apply();
    }
}
