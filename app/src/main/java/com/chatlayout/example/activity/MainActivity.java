package com.chatlayout.example.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.widget.chat.ChatLayout;
import com.actor.myandroidframework.widget.chat.OnListener;
import com.actor.myandroidframework.widget.chat.VoiceRecorderView;
import com.actor.myandroidframework.widget.chat.bean.ChatLayoutItemMore;
import com.actor.myandroidframework.widget.chat.fragment.ChatLayoutMoreFragment;
import com.actor.myandroidframework.widget.chat.utils.FaceManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chatlayout.example.R;
import com.chatlayout.example.databinding.ActivityMainBinding;
import com.chatlayout.example.utils.CheckUpdateUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private RecyclerView      recyclerView;
    private VoiceRecorderView voiceRecorder;
    private ChatLayout        chatLayout;

    private       ChatListAdapter               chatListAdapter;
    private final List<String>                  items           = new ArrayList<>();
    private final ArrayList<ChatLayoutItemMore> bottomViewDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = viewBinding.recyclerView;
        voiceRecorder = viewBinding.voiceRecorder;
        chatLayout = viewBinding.chatLayout;

        for (int i = 0; i < 20; i++) {
            items.add("Hello World!    " + i);
        }
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag ? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ChatLayoutItemMore(imgRes, "Item" + i));
        }

        chatLayout.init(recyclerView, voiceRecorder);

        //MoreFragment
        ChatLayoutMoreFragment moreFragment = ChatLayoutMoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new ChatLayoutMoreFragment.OnItemClickListener() {
            //更多点击
            @Override
            public void onItemClick(int position, ChatLayoutItemMore itemMore) {
                toast(itemMore.itemText);
            }
        });
        chatLayout.setBottomFragment(getSupportFragmentManager(), moreFragment);
        //set Tab Icon
//        chatLayout.getTabLayout().getTabAt(0).setIcon(R.drawable.emoji_small);
        TabLayout.Tab tabAt1 = chatLayout.getTabLayout().getTabAt(1);
        if (tabAt1 != null) {
            tabAt1.setIcon(R.drawable.picture);
        }

        /**
         * 设置点击事件
         */
        chatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                //点击了"发送"按钮(Send Button Click)
                String msg = getText(etMsg);
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    chatListAdapter.addData(msg);
                    recyclerView.scrollToPosition(chatListAdapter.getItemCount() - 1);
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
                chatListAdapter.addData(getStringFormat("audioPath=%s, durationMs=%d", audioPath, durationMs));
                recyclerView.scrollToPosition(chatListAdapter.getItemCount() - 1);
            }

            //录音失败, 你可以不重写这个方法(voice record failure, overrideAble)
            @Override
            public void onVoiceRecordError(Exception e) {//录音失败
                e.printStackTrace();
            }

            //还可重写其它方法override other method ...
        });

        chatListAdapter = new ChatListAdapter(items);
        chatListAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                toast(items.get(position));
            }
        });
        recyclerView.setAdapter(chatListAdapter);

        //检查更新
        new CheckUpdateUtils().check(this);
    }

    /**
     * 聊天列表的Adapter
     */
    public class ChatListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ChatListAdapter(@Nullable List<String> data) {
            super(R.layout.item_chat_contact, data);
            addChildClickViewIds(R.id.tv);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            TextView tv = helper.getView(R.id.tv);
            FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, item);
        }
    }

    /**
     * 如果BottomView == Gone,才finish()掉activity
     */
    @Override
    public void onBackPressed() {
        if (chatLayout.isBottomViewGone()) {
            super.onBackPressed();
        }
    }
}
