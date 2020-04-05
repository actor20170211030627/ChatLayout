package com.chatlayout.example.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.chatlayout.ChatLayout;
import com.actor.chatlayout.OnListener;
import com.actor.chatlayout.bean.ItemMore;
import com.actor.chatlayout.fragment.MoreFragment;
import com.actor.chatlayout.utils.FaceManager;
import com.actor.myandroidframework.widget.VoiceRecorderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chatlayout.example.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView      recyclerview;
    @BindView(R.id.voice_recorder)
    VoiceRecorderView voiceRecorder;
    @BindView(R.id.chat_layout)
    ChatLayout        chatLayout;

    private ChatListAdapter     chatListAdapter;
    private List<String>        items           = new ArrayList<>();
    private ArrayList<ItemMore> bottomViewDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        for (int i = 0; i < 20; i++) {
            items.add("Hello World!    " + i);
        }
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag ? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ItemMore(imgRes, "Item" + i));
        }

        chatLayout.init(recyclerview, voiceRecorder);

        MoreFragment moreFragment = MoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new MoreFragment.OnItemClickListener() {//更多点击
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
                chatListAdapter.addData(String.format(Locale.getDefault(), "audioPath=%s, " +
                        "durationMs=%d", audioPath, durationMs));
                recyclerview.scrollToPosition(chatListAdapter.getItemCount() - 1);
            }

            //录音失败, 你可以不重写这个方法(voice record failure, overrideAble)
            @Override
            public void onVoiceRecordError(Exception e) {//录音失败
                e.printStackTrace();
            }

            //还可重写其它方法override other method ...
        });

        chatListAdapter = new ChatListAdapter(items);
        chatListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                toast(items.get(position));
            }
        });
        recyclerview.setAdapter(chatListAdapter);
    }

    /**
     * 聊天列表的Adapter
     */
    public class ChatListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ChatListAdapter(@Nullable List<String> data) {
            super(R.layout.item_chat_contact, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            TextView tv = helper.addOnClickListener(R.id.tv).getView(R.id.tv);
            FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, item);
        }
    }

    /**
     * 如果BottomView == Gone,才finish()掉activity
     */
    @Override
    public void onBackPressed() {
        if (chatLayout.isBottomViewGone()) super.onBackPressed();
    }
}
