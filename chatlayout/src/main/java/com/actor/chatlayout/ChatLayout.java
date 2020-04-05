package com.actor.chatlayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.fragment.ChatLayoutEmojiFragment;
import com.actor.chatlayout.fragment.MoreFragment;
import com.actor.chatlayout.utils.FaceManager;
import com.actor.myandroidframework.adapter.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.utils.SPUtils;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.actor.myandroidframework.widget.VoiceRecorderView;
import com.blankj.utilcode.util.KeyboardUtils;

/**
 * description: 聊天控件,封装几个按钮及事件,包含:
 *              1.语音按钮
 *              2.EditText
 *              3.Emoji按钮
 *              4.发送按钮
 *              5.⊕按钮
 * 注意★★★: 应该重写onBackPressed方法, 示例:
 * @Override
 * public void onBackPressed() {
 *     if (chatLayout.isBottomViewGone()) {
 *         super.onBackPressed();//自己页面的逻辑
 *     }
 * }
 * author     : 李大发
 * date       : 2018/8/2 on 16:16
 * @version 1.0
 */
public class ChatLayout extends LinearLayout {

    protected ImageView    ivVoice;
    protected ImageView    ivKeyboard;
    protected EditText     etMsg;
    protected TextView     tvPressSpeak;//按住说话按钮
    protected ImageView    ivEmoji;//表情
    protected FrameLayout  flParent;
    protected Button       btnSend;
    protected ImageView    ivSendPlus;//右边⊕或ⓧ号
    protected ViewPager    viewPager;//下方的ViewPager
    protected TabLayout    tabLayout;

    protected @Nullable RecyclerView recyclerView;//上面列表RecyclerView
    protected @Nullable VoiceRecorderView       voiceRecorderView;//按住说话

    protected InputMethodManager imm;//虚拟键盘(输入法)
    protected int ivVoiceVisiable;
    protected int ivEmojiVisiable;
    protected int ivPlusVisiable;
    protected OnListener onListener;
    //键盘高度, 经我的手机测试: 手写:478 语音:477 26键:831
    protected static final String KEYBOARD_HEIGHT = "KEYBOARD_HEIGHT_FOR_CHAT_LAYOUT";

    protected boolean                    audioRecordIsCancel;//语音录制是否已取消
    protected float                      startRecordY;//按下时的y坐标
    protected AlertDialog                mPermissionDialog;
    protected @Nullable ViewPagerAdapter viewPagerAdapter;
    protected @Nullable Fragment[]       moreFragments;

    public ChatLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChatLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View inflate = View.inflate(context, R.layout.layout_for_chat_layout, this);
        ivVoice = inflate.findViewById(R.id.iv_voice_for_chat_layout);//左侧"语音"
        ivKeyboard = inflate.findViewById(R.id.iv_keyboard_for_chat_layout);//左侧"键盘"
        etMsg = inflate.findViewById(R.id.et_msg_for_chat_layout);//中间"输入框"
        tvPressSpeak = inflate.findViewById(R.id.tv_press_speak_for_chat_layout);//中间"按下说话"
        ivEmoji = inflate.findViewById(R.id.iv_emoji_for_chat_layout);//右侧"表情"
        flParent = inflate.findViewById(R.id.fl_send_plus_for_chat_layout);//"更多⊕"和"发送"按钮
        btnSend = inflate.findViewById(R.id.btn_send_for_chat_layout);//"发送"按钮
        ivSendPlus = inflate.findViewById(R.id.iv_sendplus_for_chat_layout);//"更多⊕"
        viewPager = inflate.findViewById(R.id.view_pager_for_chat_layout);//下方ViewPager
        tabLayout = inflate.findViewById(R.id.tab_layout_for_chat_layout);//最下方TabLayout
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatLayout);
            ivVoiceVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvVoiceVisiable, 0) * 4;
            ivEmojiVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvEmojiVisiable, 0) * 4;
            ivPlusVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvPlusVisiable, 0) * 4;
            Drawable background = typedArray.getDrawable(R.styleable.ChatLayout_clBtnSendBackground);
            typedArray.recycle();
            ivVoice.setVisibility(ivVoiceVisiable);//设置语音按钮是否显示
            ivEmoji.setVisibility(ivEmojiVisiable);//表情按钮是否显示
            ivSendPlus.setVisibility(ivPlusVisiable);//设置右边⊕号是否显示
            btnSend.setVisibility(ivPlusVisiable == VISIBLE ? GONE : VISIBLE);//发送按钮
            if (background != null) btnSend.setBackground(background);//背景
        }
        //监听布局变化
        KeyboardUtils.registerSoftInputChangedListener((Activity) context, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                if (height > 0) {
                    SPUtils.putInt(KEYBOARD_HEIGHT, height);
                    etMsg.requestFocus();
                    setViewPagerHeight(height);
                }
            }
        });
    }

    /**
     * 初始化
     * @param recyclerView 聊天列表, 用来设置触摸事件,响应隐藏键盘
     * @param voiceRecorderView 按住说话View
     */
    @SuppressLint("ClickableViewAccessibility")
    public void init(@Nullable RecyclerView recyclerView, @Nullable VoiceRecorderView voiceRecorderView) {
        this.recyclerView = recyclerView;
        this.voiceRecorderView = voiceRecorderView;
        if (this.recyclerView != null) {
            this.recyclerView.setOnTouchListener(new OnTouchListener() {

                private float startY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endY = event.getY();
                            if (Math.abs(endY - startY) < 15) {//点击
                                if (onListener != null) onListener.onRecyclerViewTouchListener(v, event);
                                etMsg.clearFocus();
                                setKeyBoardVisiable(false);
                                viewPager.setVisibility(GONE);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        int keyboardHeight = SPUtils.getInt(KEYBOARD_HEIGHT, 831);
        setViewPagerHeight(keyboardHeight);
        viewPager.setVisibility(GONE);
        if (voiceRecorderView != null) voiceRecorderView.setVisibility(GONE);
    }

    /**
     * 设置下方显示的emoji & more Fragment
     * @param fragmentManager Fragment管理器, Activity中传入getSupportFragmentManager()
     * @param moreFragments 更多的Fragment, 可以使用默认的{@link MoreFragment}
     */
    public void setBottomFragment(FragmentManager fragmentManager, Fragment... moreFragments) {
        this.moreFragments = moreFragments;
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager, moreFragments.length + 1);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //设置 TabLayout 的 TabItem 的 Icon
        TabLayout.Tab tabAt = tabLayout.getTabAt(0);
        if (tabAt != null) {
            if (FaceManager.emojiResShowInTabLayout != null) {
                tabAt.setIcon(FaceManager.emojiResShowInTabLayout);
            } else if (FaceManager.emojiDrawableShowInTabLayout != null) {
                tabAt.setIcon(FaceManager.emojiDrawableShowInTabLayout);
            }
        }
    }

    //ViewPager 的 Adapter
    protected class ViewPagerAdapter extends BaseFragmentStatePagerAdapter {
        protected ViewPagerAdapter(FragmentManager fm, int size) {
            super(fm, size);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0://Emoji表情Fragment
                    ChatLayoutEmojiFragment emojiFragment = ChatLayoutEmojiFragment.newInstance();
                    emojiFragment.setOnEmojiClickListener(new ChatLayoutEmojiFragment.OnEmojiClickListener() {
                        @Override
                        public void onEmojiDelete() {
                            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                            etMsg.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        }

                        @Override
                        public void onEmojiClick(Emoji emoji) {
                            int start = etMsg.getSelectionStart();
                            int end = etMsg.getSelectionEnd();
                            Editable editable = etMsg.getText();
                            if (start != end) editable.delete(start, end);//已选中
                            editable.insert(start, emoji.filter);
                            FaceManager.handlerEmojiText(etMsg, FaceManager.EMOJI_REGEX, editable);
                        }
                    });
                    return emojiFragment;
                default:
                    if (moreFragments != null) {
                        return moreFragments[position - 1];
                    } return null;
            }
        }
    }

    /**
     * 设置ViewPager高度
     */
    protected void setViewPagerHeight(int keyboardHeight) {
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();//设置高度和键盘高度一致
        if (params.height != keyboardHeight) {
            params.height = keyboardHeight;
            viewPager.setLayoutParams(params);
        }
    }

    /**
     * 填充完成
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //语音按钮
        ivVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onIvVoiceClick(ivVoice);
                //如果不设置这句,别的应用再切换过来的时候,键盘会跳出来
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                v.setVisibility(GONE);
                ivKeyboard.setVisibility(VISIBLE);
                tvPressSpeak.setVisibility(VISIBLE);
                if (ivPlusVisiable == VISIBLE) {//如果ivPlus能显示
                    btnSend.setVisibility(GONE);
                    ivSendPlus.setVisibility(VISIBLE);
                } else {//否则全隐藏,不然右侧会有个空白
                    flParent.setVisibility(GONE);
                }
                etMsg.clearFocus();
                etMsg.setVisibility(GONE);
                viewPager.setVisibility(GONE);
                setKeyBoardVisiable(false);
            }
        });

        //键盘按钮
        ivKeyboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onIvKeyBoardClick(ivKeyboard);
                v.setVisibility(GONE);
                ivVoice.setVisibility(VISIBLE);
                tvPressSpeak.setVisibility(GONE);
                etMsg.setVisibility(VISIBLE);
                flParent.setVisibility(VISIBLE);
                //如果ivPlus不显示 或者 EditText里有字,都要显示发送按钮
                if (ivPlusVisiable != VISIBLE || etMsg.getText().toString().length() > 0) btnSend.setVisibility(VISIBLE);
                etMsg.requestFocus();
                // 输入法弹出之后，重新调整
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                setKeyBoardVisiable(true);
            }
        });

        //语音按钮
        tvPressSpeak.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (onListener != null) {
                    onListener.onTvPressSpeakTouch(tvPressSpeak, event);
                    //如果语音按钮显示 && 按下录音View不为空
                    if (ivVoiceVisiable == VISIBLE && voiceRecorderView != null) {
                        if (!checkStoragePermisson(Manifest.permission.RECORD_AUDIO)) {
                            onListener.onNoPermission(Manifest.permission.RECORD_AUDIO);
                        } else if (!checkStoragePermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            onListener.onNoPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        } else if (!checkStoragePermisson(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            onListener.onNoPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    audioRecordIsCancel = false;
                                    startRecordY = event.getY();
                                    voiceRecorderView.startRecording();
                                    AudioUtils.getInstance().startRecord(new AudioUtils.AudioRecordCallback() {

                                        @Override
                                        public void recordComplete(String audioPath,
                                                                   long durationMs) {
                                            if (audioRecordIsCancel) {
                                                voiceRecorderView.stopRecording();
                                                return;
                                            }
                                            if (durationMs < 500) {
                                                voiceRecorderView.tooShortRecording();
                                                return;
                                            }
                                            voiceRecorderView.stopRecording();
                                            String recordAudioPath =//语音路径
                                                    AudioUtils.getInstance().getRecordAudioPath();
                                            if (!TextUtils.isEmpty(recordAudioPath))
                                                onListener.onVoiceRecordSuccess(recordAudioPath, durationMs);
                                        }

                                        @Override
                                        public void recordCancel(String audioPath, long durationMs) {
                                            voiceRecorderView.stopRecording();
                                        }

                                        @Override
                                        public void recordError(final Exception e) {//子线程
                                            post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    voiceRecorderView.stopRecording();
                                                    onListener.onVoiceRecordError(e);
                                                }
                                            });
                                        }
                                    });
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (event.getY() - startRecordY < -100) {
                                        audioRecordIsCancel = true;
                                        voiceRecorderView.release2CancelRecording();//松开手指取消发送
                                    } else {
                                        audioRecordIsCancel = false;
                                        voiceRecorderView.startRecording();//开始录音
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
//                                    if (event.getY() - startRecordY < -100) {
//                                        audioRecordIsCancel = true;
//                                    } else {
//                                        audioRecordIsCancel = false;
//                                    }
                                    AudioUtils.getInstance().stopRecord(audioRecordIsCancel);
                                    break;
                            }
                        }
                    }
                }
                return true;
            }
        });

        //EditText如果没有焦点的时候,onClick点击事件不回调,所以用这个onTouch事件
        etMsg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (onListener != null) onListener.onEditTextToucn(etMsg, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (viewPager.getVisibility() != GONE) {
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                    etMsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewScroll2Last(300);
                            if (viewPager != null) viewPager.setVisibility(View.GONE);
                            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        }
                    }, 250); // 延迟一段时间，等待输入法完全弹出
                }
                return true;
            }
        });

        //文字改变监听,用于切换"发送按钮"和"右侧⊕",所以ivPlus能显示时才设置监听
        if (ivPlusVisiable == VISIBLE) {
            etMsg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        ivSendPlus.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                    } else {
                        ivSendPlus.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        //Emoji表情
        ivEmoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) {
                    onListener.onIvEmojiClick(ivEmoji);
                }
                onEmoji$PlusClicked(true);
            }
        });

        //发送按钮
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onBtnSendClick(etMsg);
            }
        });

        //右边⊕号
        ivSendPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) {
                    onListener.onIvPlusClick(ivSendPlus);
                }
                onEmoji$PlusClicked(false);
            }
        });

        if(!isInEditMode()){//造成错误的代码段
        }
    }

    //检查权限, 返回是否有权限
    public boolean checkStoragePermisson(String permisson) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(getContext(), permisson);
            return PackageManager.PERMISSION_GRANTED == permission;
        }
        return true;
    }
    //显示没有权限的对话框, 跳转设置界面
    public void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(getContext())
                    .setMessage("使用该功能，需要开启权限，鉴于您禁用相关权限，请手动设置开启权限")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Uri packageURI = Uri.parse("package:".concat(getContext().getPackageName()));
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            getContext().startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //当 表情 or Plus按钮点击的时候
    protected void onEmoji$PlusClicked(boolean isClickEmoji) {
        if (ivVoiceVisiable == VISIBLE) ivVoice.setVisibility(VISIBLE);
        ivKeyboard.setVisibility(GONE);
        etMsg.setVisibility(VISIBLE);
        tvPressSpeak.setVisibility(GONE);
        flParent.setVisibility(VISIBLE);
        int selectedTabPosition = tabLayout.getSelectedTabPosition();
        //切换到某个Fragment
        TabLayout.Tab tabAt = tabLayout.getTabAt(isClickEmoji ? 0 : 1);
        if (tabAt != null) tabAt.select();

        //如果ivPlust不显示 or EditText里有内容
        if (ivPlusVisiable != VISIBLE || etMsg.getText().toString().length() > 0) {
            btnSend.setVisibility(VISIBLE);
        }
        if (KeyboardUtils.isSoftInputVisible((Activity) getContext())) {//输入法打开状态下
            // 设置为不会调整大小，以便输入法弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            viewPager.setVisibility(VISIBLE);
            setKeyBoardVisiable(false);
            recyclerViewScroll2Last(0);
        } else {//输入法关闭状态下
            if (viewPager.getVisibility() != VISIBLE) {//bottomView是隐藏状态
                viewPager.setVisibility(VISIBLE);
                recyclerViewScroll2Last(0);
            } else {
                if (isClickEmoji) {//如果点击的是Emoji
                    if (selectedTabPosition == 0) {//已经选中了Emoji: 显示键盘, 隐藏ViewPager
                        // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        setKeyBoardVisiable(true);
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() { //输入法弹出之后，重新调整
                                viewPager.setVisibility(View.GONE);
                                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            }
                        }, 250); // 延迟一段时间，等待输入法完全弹出
                        etMsg.requestFocus();
                    }
                } else {//如果点击的是⊕
                    if (selectedTabPosition != 0) {//已经选中了⊕: 显示键盘, 隐藏ViewPager
                        // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        setKeyBoardVisiable(true);
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() { //输入法弹出之后，重新调整
                                viewPager.setVisibility(View.GONE);
                                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            }
                        }, 250); // 延迟一段时间，等待输入法完全弹出
                        etMsg.requestFocus();
                    }
                }

            }
        }
    }

    /**
     * 设置点击事件&其它事件的监听
     */
    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    /**
     * 获取语音ImageView
     */
    public ImageView getIvVoice() {
        return ivVoice;
    }

    /**
     * 获取键盘ImageView
     */
    public ImageView getIvKeyBoard() {
        return ivKeyboard;
    }

    /**
     * 获取输入框EditText
     */
    public EditText getEditText() {
        return etMsg;
    }

    /**
     * 获取按住说话TextView
     */
    public TextView getTvPressSpeak() {
        return tvPressSpeak;
    }

    /**
     * 获取右侧Emoji☺ImageView
     */
    public ImageView getIvEmoji() {
        return ivEmoji;
    }

    /**
     * 获取发送按钮Button
     * @return
     */
    public Button getBtnSend() {
        return btnSend;
    }

    /**
     * 获取右侧⊕ImageView
     */
    public ImageView getIvPlus() {
        return ivSendPlus;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    /**
     * 如果下方控件没有隐藏,就隐藏:if (clChatLayout.isBottomViewGone()) super.onBackPressed();
     * @return 是否已经处理完成
     */
    public boolean isBottomViewGone() {
        if (viewPager.getVisibility() != GONE) {
            viewPager.setVisibility(GONE);
            return false;
        }
        return true;
    }

    //设置键盘是否显示
    protected boolean setKeyBoardVisiable(boolean isVisiable) {
        if (isVisiable) {
            recyclerViewScroll2Last(300);
            return imm.showSoftInput(etMsg, 0);
        } else {
            return imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
        }
    }

    protected void setSoftInputMode(int mode) {
        ((Activity) getContext()).getWindow().setSoftInputMode(mode);
    }

    /**
     * RecyclerView滚动到最后
     * @param delay 延时多少秒后滚动到最后
     */
    protected void recyclerViewScroll2Last(int delay) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            final int itemCount = adapter.getItemCount();
            if (itemCount > 0) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView != null) {
                            recyclerView.scrollToPosition(itemCount - 1);
                        }
                    }
                }, delay);//等输入法弹出后, 再滑动到最后
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        KeyboardUtils.unregisterSoftInputChangedListener(((Activity) getContext()).getWindow());
        AudioUtils.getInstance().stopRecord(true);
        AudioUtils.getInstance().stopPlayRecord();
        super.onDetachedFromWindow();
    }
}
