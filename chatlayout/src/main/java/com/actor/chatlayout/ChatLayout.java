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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.fragment.EmojiFragment;
import com.actor.chatlayout.fragment.MoreFragment;
import com.actor.chatlayout.utils.FaceManager;
import com.actor.chatlayout.utils.KeyboardUtils;

/**
 * <ul>
 *     <li>Description: 聊天控件,封装几个按钮及事件,包含:
 *     <ol>
 *         <li>语音按钮</li>
 *         <li>EditText</li>
 *         <li>Emoji按钮</li>
 *         <li>发送按钮</li>
 *         <li>⊕按钮</li>
 *     </ol>
 *     </li>
 *     <li>★★★注意:应该重写onBackPressed方法,示例:★★★</li>
 *     <li>&emsp;@Override</li>
 *     <li>&emsp;public void onBackPressed() {</li>
 *     <li>&emsp;&emsp;if (chatLayout.isBottomViewGone()) {</li>
 *     <li>&emsp;&emsp;&emsp;super.onBackPressed();//自己页面的逻辑</li>
 *     <li>&emsp;&emsp;}</li>
 *     <li>&emsp;}</li>
 *     <li></li>
 *     <li>Copyright  : Copyright (c) 2018</li>
 *     <li>Author     : actor</li>
 *     <li>Date       : 2018/8/2 on 16:16</li>
 * </ul>
 */
public class ChatLayout extends LinearLayout {

    private static final String TAG = ChatLayout.class.getName();
    private View listView;//上面列表View
    private ImageView ivVoice;
    private ImageView ivKeyboard;
    private EditText etMsg;
    private TextView tvPressSpeak;//按住说话按钮
    private ImageView ivEmoji;//表情
    private FrameLayout flParent;
    private Button btnSend;
    private ImageView ivSendPlus;//右边⊕或ⓧ号
    private View bottomView;//底部View

    private InputMethodManager imm;//虚拟键盘(输入法)
    private boolean ivVoiceVisiable;
    private boolean ivEmojiVisiable;
    private boolean ivPlusVisiable;

    private OnListener onListener;
    private boolean isKeyboardActive = false; //输入法是否激活
    private KeyboardOnGlobalChangeListener keyboardOnGlobalChangeListener;//onDetachedFromWindow中注销

    private VoiceRecorderView voiceRecorderView;//按住说话
    private boolean           audioRecordIsCancel;//语音录制是否已取消
    private float             startRecordY;//按下时的y坐标
    private AlertDialog       mPermissionDialog;
    private FragmentManager   fragmentManager;//用来控制下方emoji & ⊕ 的显示和隐藏
    private EmojiFragment     emojiFragment;
    private MoreFragment moreFragment;

    public ChatLayout(Context context) {
        this(context, null);
    }

    public ChatLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        /**
         * 给当前空的布局填充内容
         * 参3:A view group that will be the parent.
         * 传null表示当前布局没有父控件,大部分都传null
         * 传this表示已当前相对布局为这个布局的父控件,这样做了以后,当前空的布局就有内容了
         */
        //1.给SettingItemView(RelatuveLayout)填充布局
        View inflate = View.inflate(context, R.layout.layout_center_buttons, this);
        ivVoice = inflate.findViewById(R.id.iv_voice);
        ivKeyboard = inflate.findViewById(R.id.iv_keyboard);
        etMsg = inflate.findViewById(R.id.et_msg);
        tvPressSpeak = inflate.findViewById(R.id.tv_press_speak);
        ivEmoji = inflate.findViewById(R.id.iv_emoji);
        flParent = inflate.findViewById(R.id.fl_parent);
        btnSend = inflate.findViewById(R.id.btn_send);
        ivSendPlus = inflate.findViewById(R.id.iv_sendplus);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ChatLayout);
            ivVoiceVisiable = typedArray.getBoolean(R.styleable.ChatLayout_clIvVoiceVisiable, true);
            ivEmojiVisiable = typedArray.getBoolean(R.styleable.ChatLayout_clIvEmojiVisiable, true);
            ivPlusVisiable = typedArray.getBoolean(R.styleable.ChatLayout_clIvPlusVisiable, true);
            typedArray.recycle();
            ivVoice.setVisibility(ivVoiceVisiable ? VISIBLE : GONE);//设置语音按钮是否显示
            ivEmoji.setVisibility(ivEmojiVisiable ? VISIBLE : GONE);//表情按钮是否显示
            ivSendPlus.setVisibility(ivPlusVisiable ? VISIBLE : GONE);//设置右边⊕号是否显示
            btnSend.setVisibility(ivPlusVisiable ? GONE : VISIBLE);
        }

        //监听布局变化
        keyboardOnGlobalChangeListener = new KeyboardOnGlobalChangeListener();
        getViewTreeObserver().addOnGlobalLayoutListener(keyboardOnGlobalChangeListener);
    }

    /**
     * 初始化
     * @param mListView 用来设置触摸事件,响应隐藏键盘
     * @param mBottomView 用来设置和键盘一样的高度
     * @param voiceRecorderView 按住说话View
     */
    public void init(View mListView, View mBottomView, VoiceRecorderView voiceRecorderView) {
        this.listView = mListView;
        this.bottomView = mBottomView;
        this.voiceRecorderView = voiceRecorderView;
        if (listView != null) {
            listView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onListener != null) onListener.onListViewTouchListener(v, event);
                    etMsg.clearFocus();
                    setKeyBoardVisiable(false);
                    if (bottomView != null) bottomView.setVisibility(GONE);
                    return false;
                }
            });
        }

        if (bottomView != null) {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            int keyboardHeight = KeyboardUtils.getKeyboardHeight();
            if (keyboardHeight > 0) {
                ViewGroup.LayoutParams params = bottomView.getLayoutParams();//设置高度和键盘高度一致
                params.height = keyboardHeight;
                bottomView.setLayoutParams(params);
                bottomView.setVisibility(GONE);
            }
        }
        if (voiceRecorderView != null) voiceRecorderView.setVisibility(GONE);
        UIKitAudioArmMachine.init(getContext(), null);//初始化录音, 默认最大录音时长2分钟
    }

    /**
     * 设置下方显示的emoji & more Fragment
     * @param fragmentManager
     * @param emojiFragment
     * @param moreFragment
     */
    public void setBottomFragments(FragmentManager fragmentManager, EmojiFragment emojiFragment, MoreFragment moreFragment) {
        this.fragmentManager = fragmentManager;
        this.emojiFragment = emojiFragment;
        this.moreFragment = moreFragment;

        this.emojiFragment.setListener(new EmojiFragment.OnEmojiClickListener() {
            @Override
            public void onEmojiDelete() {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                etMsg.dispatchKeyEvent(event);
            }

            @Override
            public void onEmojiClick(Emoji emoji) {
                int index = etMsg.getSelectionStart();
                Editable editable = etMsg.getText();
                editable.insert(index, emoji.filter);
                FaceManager.handlerEmojiText(etMsg, editable.toString());
            }

            @Override
            public void onCustomFaceClick(int groupIndex, Emoji emoji) {
                // TODO: 2019/6/3
                Log.e(TAG, "onCustomFaceClick: 自定义表情, 还未实现");
            }
        });

//        if (bottomView != null && fragmentManager != null) {
//            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
//            if (this.emojiFragment != null) {
//                fragmentTransaction.add(bottomView.getId(), this.emojiFragment)
//                        .hide(this.emojiFragment);
//            }
//            if (this.moreFragment != null) {
//                fragmentTransaction.add(bottomView.getId(), this.moreFragment)
//                        .hide(this.moreFragment);
//            }
//            fragmentTransaction.commit();
//        }
    }

    private Rect rect = new Rect();
    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            if (onListener != null) onListener.onGlobalLayout();
            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > 500) {//手写:478 语音:477 26键:831.screenHeight / 5
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
                KeyboardUtils.saveKeyboardHeight(keyboardHeight);
            }
            isKeyboardActive = isActive;
            if (isKeyboardActive) {
                etMsg.requestFocus();
                if (bottomView != null) {
                    ViewGroup.LayoutParams params = bottomView.getLayoutParams();
                    if (!(params.height == keyboardHeight)) {
                        params.height = keyboardHeight;
                        bottomView.setLayoutParams(params);
                    }
                }
            }
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
                if (ivPlusVisiable) {//如果ivPlus能显示
                    btnSend.setVisibility(GONE);
                    ivSendPlus.setVisibility(VISIBLE);
                } else {//否则全隐藏,不然右侧会有个空白
                    flParent.setVisibility(GONE);
                }
                etMsg.clearFocus();
                etMsg.setVisibility(GONE);
                if (bottomView != null) bottomView.setVisibility(GONE);
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
                if (!ivPlusVisiable || etMsg.getText().toString().length() > 0) btnSend.setVisibility(VISIBLE);
                etMsg.requestFocus();
                // 输入法弹出之后，重新调整
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                    if (ivVoiceVisiable && voiceRecorderView != null) {
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
                                    UIKitAudioArmMachine.getInstance().startRecord(new UIKitAudioArmMachine.AudioRecordCallback() {

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
                                                    UIKitAudioArmMachine.getInstance().getRecordAudioPath();
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
                                    UIKitAudioArmMachine.getInstance().stopRecord(audioRecordIsCancel);
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
                    if (bottomView != null && bottomView.getVisibility() != GONE) {
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                    etMsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (bottomView != null) bottomView.setVisibility(View.GONE);
                            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        }
                    }, 250); // 延迟一段时间，等待输入法完全弹出
                }
                return true;
            }
        });

        //文字改变监听,用于切换"发送按钮"和"右侧⊕",所以ivPlus能显示时才设置监听
        if (ivPlusVisiable) {
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
                    if (bottomView != null && fragmentManager != null) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (moreFragment != null) {
//                            if (!moreFragment.isAdded()) transaction.add(bottomView.getId(), moreFragment);
                            if (moreFragment.isAdded() && !moreFragment.isHidden()) {
                                transaction.hide(moreFragment);
                                moreFragment.setUserVisibleHint(false);
                            }
                        }
                        if (emojiFragment != null) {
                            if (!emojiFragment.isAdded()) transaction.add(bottomView.getId(), emojiFragment);
                            transaction.show(emojiFragment);
                            emojiFragment.setUserVisibleHint(true);
                        }
                        transaction.commit();
                    }
                }
                onEmoji$PlusClicked();
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
                    if (bottomView != null && fragmentManager != null) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (emojiFragment != null) {
                            if (emojiFragment.isAdded() && !emojiFragment.isHidden()) {
                                transaction.hide(emojiFragment);
                                emojiFragment.setUserVisibleHint(false);//fragment.onHiddenChanged
                            }
                        }
                        if (moreFragment != null) {
                            if (!moreFragment.isAdded()) transaction.add(bottomView.getId(), moreFragment);
                            transaction.show(moreFragment);
                            moreFragment.setUserVisibleHint(true);
                        }
                        transaction.commit();
                    }
                }
                onEmoji$PlusClicked();
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

    //当键盘 or 表情 or Plus按钮点击的时候
    private void onEmoji$PlusClicked() {
        if (ivVoiceVisiable) ivVoice.setVisibility(VISIBLE);
        ivKeyboard.setVisibility(GONE);
        etMsg.setVisibility(VISIBLE);
        tvPressSpeak.setVisibility(GONE);
        flParent.setVisibility(VISIBLE);
        //如果ivPlust不显示 or EditText里有内容
        if (!ivPlusVisiable || etMsg.getText().toString().length() > 0) {
            btnSend.setVisibility(VISIBLE);
        }
        if (isKeyboardActive) {//输入法打开状态下
            // 设置为不会调整大小，以便输入法弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            setKeyBoardVisiable(false);
            if (bottomView != null) bottomView.setVisibility(VISIBLE);
        } else {//输入法关闭状态下
            if (bottomView != null) {
                if (bottomView.getVisibility() != VISIBLE) {
                    bottomView.setVisibility(VISIBLE);
                } else {
                    // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    setKeyBoardVisiable(true);
                    bottomView.postDelayed(new Runnable() {
                        @Override
                        public void run() { //输入法弹出之后，重新调整
                            bottomView.setVisibility(View.GONE);
                            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        }
                    }, 250); // 延迟一段时间，等待输入法完全弹出
                    etMsg.requestFocus();
                }
            }
        }
    }

    /**
     * 设置点击事件&其它事件的监听
     * @param onListener
     */
    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    /**
     * 获取语音ImageView
     * @return
     */
    public ImageView getIvVoice() {
        return ivVoice;
    }

    /**
     * 获取键盘ImageView
     * @return
     */
    public ImageView getIvKeyBoard() {
        return ivKeyboard;
    }

    /**
     * 获取输入框EditText
     * @return
     */
    public EditText getEditText() {
        return etMsg;
    }

    /**
     * 获取按住说话TextView
     * @return
     */
    public TextView getTvPressSpeak() {
        return tvPressSpeak;
    }

    /**
     * 获取右侧Emoji☺ImageView
     * @return
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
     * @return
     */
    public ImageView getIvPlus() {
        return ivSendPlus;
    }

    /**
     * 如果下方控件没有隐藏,就隐藏:if (clChatLayout.isBottomViewGone()) super.onBackPressed();
     * @return 是否已经处理完成
     */
    public boolean isBottomViewGone() {
        if (bottomView != null && bottomView.getVisibility() != GONE) {
            bottomView.setVisibility(GONE);
            return false;
        }
        return true;
    }

    //设置键盘是否显示
    private boolean setKeyBoardVisiable(boolean isVisiable) {
        isKeyboardActive = isVisiable;
        if (isVisiable) {
            return imm.showSoftInput(etMsg, 0);
        } else {
            return imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
        }
    }

    /**
     * 获取屏幕高度
     */
    private int mScreenHeight = 0;
    private int getScreenHeight() {
        if (mScreenHeight > 0) return mScreenHeight;
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    private void setSoftInputMode(int mode) {
        ((Activity) getContext()).getWindow().setSoftInputMode(mode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (keyboardOnGlobalChangeListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getViewTreeObserver().removeOnGlobalLayoutListener(keyboardOnGlobalChangeListener);
            }
            keyboardOnGlobalChangeListener = null;
        }
        UIKitAudioArmMachine.getInstance().stopRecord(true);
        UIKitAudioArmMachine.getInstance().stopPlayRecord();
        if (fragmentManager != null && bottomView != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (emojiFragment != null && emojiFragment.isAdded()) transaction.remove(emojiFragment);
            if (moreFragment != null && moreFragment.isAdded()) transaction.remove(moreFragment);
            transaction.commit();
        }
    }
}
