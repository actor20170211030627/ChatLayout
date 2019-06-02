package com.actor.chatlayout;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Description: 来自腾讯TUIKit Demo中的录音和播放
 * 需要权限:
 *
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/5/30 on 17:43
 */
public class UIKitAudioArmMachine {

    private boolean playing, innerRecording;
    private volatile Boolean             recording           = false;
    public static    String              CURRENT_RECORD_FILE;//语音存储路径
    private          AudioRecordCallback mRecordCallback;
    private          AudioPlayCallback   mPlayCallback;

    private String recordAudioPath;
    private long   startTime, endTime;
    private        MediaPlayer          mPlayer;
    private        MediaRecorder        mRecorder;
    private static UIKitAudioArmMachine instance;
    private static int maxRecordTime = 2 * 60 * 1000;//最大录音时长, 默认2分钟

    private UIKitAudioArmMachine() {
    }

    /**
     * 初始化
     * @param context
     * @param maxRecordTimeSecond 最大录音时长, 单位秒, 默认2分钟, 可以传null
     */
    public static void init(Context context, @Nullable Integer maxRecordTimeSecond) {
        if (CURRENT_RECORD_FILE == null) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    .concat("/").concat(context.getPackageName()).concat("/record/");
            File file = new File(path);
            if (!file.exists()) file.mkdirs();
            CURRENT_RECORD_FILE = path.concat("auto_");
        }
        if (maxRecordTimeSecond != null) maxRecordTime = maxRecordTimeSecond * 1000;
    }

    public static UIKitAudioArmMachine getInstance() {
        if (instance == null) instance = new UIKitAudioArmMachine();
        return instance;
    }

    /**
     * 开始录音
     */
    public void startRecord(AudioRecordCallback callback) {
        synchronized (recording) {
            mRecordCallback = callback;
            recording = true;
            new RecordThread().start();
        }
    }

    /**
     * 结束录音
     */
    public void stopRecord() {
        synchronized (recording) {
            if (recording) {
                recording = false;
                endTime = System.currentTimeMillis();
                if (mRecordCallback != null) mRecordCallback.recordComplete(endTime - startTime);
                if (mRecorder != null && innerRecording) {
                    try {
                        innerRecording = false;
                        mRecorder.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 播放录音
     */
    public void playRecord(String filePath, AudioPlayCallback callback) {
        this.mPlayCallback = callback;
        new PlayThread(filePath).start();
    }

    /**
     * 停止播放录音
     */
    public void stopPlayRecord() {
        if (mPlayer != null) {
            mPlayer.stop();
            playing = false;
            if (mPlayCallback != null) mPlayCallback.playComplete(null);
        }
    }

    public boolean isPlayingRecord() {
        return playing;
    }


    public String getRecordAudioPath() {
        return recordAudioPath;
    }

    //ms
    public long getDuration() {
        return endTime - startTime;
    }

    public interface AudioRecordCallback {
        void recordComplete(long duration);
        void recordError(Exception e);//录音失败
    }

    public interface AudioPlayCallback {
        void playComplete(@Nullable String audioPath);
        void playError(String audioPath, String errorReason);
    }


    private class RecordThread extends Thread {
        @Override
        public void run() {
            //根据采样参数获取每一次音频采样大小
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //RAW_AMR虽然被高版本废弃，但它兼容低版本还是可以用的
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                recordAudioPath = CURRENT_RECORD_FILE + System.currentTimeMillis();
                mRecorder.setOutputFile(recordAudioPath);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                startTime = System.currentTimeMillis();
                synchronized (recording) {
                    if (!recording) return;
                    mRecorder.prepare();
                    mRecorder.start();
                }
                innerRecording = true;
                new Thread() {
                    @Override
                    public void run() {
                        while (recording && innerRecording) {
                            try {
                                RecordThread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //TencentImUtils.getBaseConfigs().getAudioRecordMaxTime() * 1000
                            if (System.currentTimeMillis() - startTime >= maxRecordTime) {
                                stopRecord();
                                return;
                            }
                        }
                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
                if (mRecordCallback != null) mRecordCallback.recordError(e);
            }

        }
    }


    private class PlayThread extends Thread {
        String audioPath;

        PlayThread(String filePath) {
            audioPath = filePath;
        }

        public void run() {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(audioPath);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mPlayCallback != null) mPlayCallback.playComplete(audioPath);
                        playing = false;
                    }
                });
                mPlayer.prepare();
                mPlayer.start();
                playing = true;
            } catch (Exception e) {
                e.printStackTrace();
                if (mPlayCallback != null) mPlayCallback.playError(audioPath, "语音文件已损坏或不存在");
                playing = false;
            }
        }
    }
}
