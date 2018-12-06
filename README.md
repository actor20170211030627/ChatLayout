# ChatLayout-master
聊天界面按钮的简单封装...

## Screenshot
<img src="captures/example.gi"></img>

## Demo
<a href="captures/app-debug.apk" target="_blank">download apk</a>

## Usage
<ol>
	<li> in xml
	<pre>
		< com.actor.chatlayout.ChatLayout
		    android:id="@+id/cl_chatLayout"
		    android:layout_width="match_parent"
		    android:layout_height="wrap_content"
		    app:clIvEmojiVisiable="true"	//表情(Emoji) Visiable(true default)
		    app:clIvPlusVisiable="true"		//右侧⊕号(Plus) Visiable(true default)
		    app:clIvVoiceVisiable="true">	//语音(Voice) Visiable(true default)
		< /com.actor.chatlayout.ChatLayout>
	</pre>
	</li>
	<li>in activity
<pre>
    private RecyclerView rvRecyclerview;//chat list(这是上面的聊天列表)
    private ChatLayout clChatLayout;
    private RecyclerView rvBottom;//bottom view,height=keyboard's height(这是下面的view,会动态设置和键盘一样的高度)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvRecyclerview = (RecyclerView) findViewById(R.id.rv_recyclerview);
        <font color=#A52A2A>clChatLayout</font> = (ChatLayout) findViewById(R.id.cl_chatLayout);
        rvBottom = (RecyclerView) findViewById(R.id.rv_bottom);

        //1.init, nullable(初始化,可以都传null)
        <font color=#A52A2A>clChatLayout</font>.init(rvRecyclerview, rvBottom);

        //2.setListener,u can override other method(还有一些方法,如果需要自己重写)
        <font color=#A52A2A>clChatLayout</font>.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                String msg = etMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    mDatas.add(msg);
                    chatListAdapter.notifyItemInserted(chatListAdapter.getItemCount() - 1);
                    rvRecyclerview.scrollToPosition(chatListAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onIvEmojiClick(ImageView ivEmoji) {
                super.onIvEmojiClick(ivEmoji);
                toast("Emoji Click");
            }

            @Override
            public void onIvPlusClick(ImageView ivPlus) {
                super.onIvPlusClick(ivPlus);
                toast("Plus Click");
            }
        });
    }

    @Override
    public void onBackPressed() {
        //3.if bottom view == Gone,finish()
        if (<font color=#A52A2A>clChatLayout</font>.isBottomViewGone()) super.onBackPressed();
    }
}
</pre>
	</li>
</ol>


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

	dependencies {
	        implementation 'com.github.actor20170211030627:ChatLayout:version'
	}
