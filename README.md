# ChatLayout
聊天界面按钮的简单封装...

## Screenshot
<img src="captures/example.gif" width=35%></img>

## Demo
<a href="https://github.com/actor20170211030627/ChatLayout/blob/master/captures/app-debug.apk" target="_blank">download apk</a>

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

## Thanks
<a href="https://github.com/604982372/InputConflict" target="_blank">604982372/InputConflict</a>

<h1><a id="user-content-license" class="anchor" aria-hidden="true" href="#license"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path></svg></a>License</h1>
<pre><code>Copyright 2018 actor20170211030627

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</code></pre>