package com.chatlayout.example.info;

/**
 * description: 主页消息
 *
 * @author : ldf
 * date       : 2021/5/26 on 16
 * @version 1.0
 */
public class MessageItem {

    public String message;

    public String audioPath;
    public long durationMs;

    public MessageItem(String message) {
        this.message = message;
    }

    public MessageItem(String audioPath, long durationMs) {
        this.audioPath = audioPath;
        this.durationMs = durationMs;
    }
}
