# ChatLayout
聊天界面按钮的简单封装...
 <ul>
     <li><a href="https://github.com/actor20170211030627/ChatLayout">Github</a></li>
     <li><a href="https://gitee.com/actor2017/ChatLayout">Gitee码云</a></li>
 </ul>

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Screenshot
#### 1.chatlayout ↓
<img src="captures/chatlayout.png" width=35%></img>

#### 2.example gif
<img src="captures/example.gif" width=35%></img>

## Demo
<a href="https://github.com/actor20170211030627/ChatLayout/raw/master/app/build/outputs/apk/debug/app-debug.apk">download apk</a> or scan qrcode:  <br/>
<img src="captures/qrcode.png" width=35%></img>

## Usage
**1.** 在Application中初始化

    ChatLayoutKit.init(getApplication(), true);//初始化
    //初始化语音, 默认最大录音时长2分钟. 如果不用语音, 不用初始化
    AudioUtils.getInstance().init(null, null);

**2.** 布局文件中xml

    <!--1.其它布局有可能会有bug, 根部局建议用LinearLayout. (other root layout maybe bug)-->
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
    
        <TextView
            android:id="@+id/tv_title"
            android:layout_width="match_parent"
            android:layout_height="?android:actionBarSize"
            android:background="@color/colorPrimary"
            android:gravity="center"
            android:text="Title"
            android:textColor="@color/white"
            android:textSize="18sp" />
    
        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">
    
            <!--2.聊天列表(Chat list)-->
            <android.support.v7.widget.RecyclerView
                android:id="@+id/rv_recyclerview"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:layoutManager="android.support.v7.widget.LinearLayoutManager"
                app:stackFromEnd="true"
                tools:listitem="@layout/item_chat_contact" />
    
            <!--3.按住说话(Hold To Talk)-->
            <com.actor.myandroidframework.widget.VoiceRecorderView
                android:id="@+id/voice_recorder"
                android:layout_width="200dp"
                android:layout_height="200dp"
                android:layout_gravity="center" />
        </FrameLayout>
    
        <!--4.-->
        <com.actor.chatlayout.ChatLayout
            android:id="@+id/cl_chatLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:clBtnSendBackground=""  //发送按钮背景(Send Button's background), 默认@drawable/selector_btn_send_for_chat_layout(default)
            app:clIvEmojiVisiable=""    //表情图片是否显示(emoji image visiable), 默认visible(default)
            app:clIvPlusVisiable=""     //⊕图片是否显示(⊕ image visiable), 默认visible(default)
            app:clIvVoiceVisiable="" /> //语音图片是否显示(voice image visiable), 默认visible(default)
    </LinearLayout>

**3.** Activity中

    private RecyclerView         recyclerview;
    private VoiceRecorderView    voiceRecorder;
    private ChatLayout           chatLayout;
    private ArrayList<ItemMore>  bottomViewDatas = new ArrayList<>();
    private ChatListAdapter      chatListAdapter;
    
    protected void onCreate(Bundle savedInstanceState) {
        ...
        //初始化"⊕更多"(init "more")
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ItemMore(imgRes, "Item" + i));
        }
        
        chatLayout.init(recyclerview, voiceRecorder);
        
        MoreFragment moreFragment = MoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new MoreFragment.OnItemClickListener() {//更多点击(click ⊕)
            @Override
            public void onItemClick(int position, ItemMore itemMore) {
                toast(itemMore.itemText);
            }
        });
        chatLayout.setBottomFragment(getSupportFragmentManager(), moreFragment);
        //set Tab1 Icon
		chatLayout.getTabLayout().getTabAt(1).setIcon(R.drawable.picture);

        chatLayout.setOnListener(new OnListener() {
            
            //点击了"发送"按钮(Send Button Click)
            @Override
            public void onBtnSendClick(EditText etMsg) {
                String msg = getText(etMsg);
				if (!TextUtils.isEmpty(msg)) {
				    etMsg.setText("");
				    chatListAdapter.addData(msg);
				    recyclerview.scrollToPosition(chatListAdapter.getItemCount() - 1);
				}
            }

            //点击了"表情"按钮, 你可以不重写这个方法(overrideAble)
            @Override
            public void onIvEmojiClick(ImageView ivEmoji) {
                toast("Emoji Click");
            }

            //点击了"⊕"按钮, 你可以不重写这个方法(overrideAble)
            @Override
            public void onIvPlusClick(ImageView ivPlus) {
                toast("Plus Click");
            }

            //没语音权限, 你可以不重写这个方法(no voice record permissions, overrideAble)
            @Override
            public void onNoPermission(String permission) {
                //可以调用默认处理方法. 你也可以不调用这个方法, 自己处理(call default request permission method, or deal by yourself)
                chatLayout.showPermissionDialog();
            }

            //录音成功, 你可以不重写这个方法(voice record success, overrideAble)
            @Override
            public void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs) {
                toast(String.format(Locale.getDefault(), "audioPath=%s, durationMs=%d", audioPath, durationMs));
            }

            //录音失败, 你可以不重写这个方法(voice record failure, overrideAble)
            @Override
            public void onVoiceRecordError(Exception e) {
                e.printStackTrace();
            }

            //还可重写其它方法(you can override other methods ...)
            ...
        });
        chatListAdapter = new ChatListAdapter();
        rvRecyclerview.setAdapter(chatListAdapter);
    }

    @Override
    public void onBackPressed() {
        if (chatLayout.isBottomViewGone()) super.onBackPressed();
    }


## How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
<pre>
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
</pre>


**Step 2.** Add the dependency, the last version:
[![](https://jitpack.io/v/actor20170211030627/ChatLayout.svg)](https://jitpack.io/#actor20170211030627/ChatLayout)

    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
        implementation 'com.android.support:appcompat-v7:your_version'
        implementation 'com.android.support:recyclerview-v7:your_version'
        implementation 'com.android.support.constraint:constraint-layout:your_version'

        //https://github.com/actor20170211030627/MyAndroidFrameWork
        implementation 'com.github.actor20170211030627:MyAndroidFrameWork:1.3.4'

        //https://github.com/actor20170211030627/ChatLayout
        implementation 'com.github.actor20170211030627:ChatLayout:version'
    }

## Thanks
<ul>
    <li>keyboard from: <a href="https://github.com/604982372/InputConflict" target="_blank">604982372/InputConflict</a> </li>
    <li>emoji from: <a href="https://github.com/tencentyun/TIMSDK" target="_blank">tencentyun/TIMSDK</a></li>
</ul>

## License
 Apache 2.0.
