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

    private ChatListAdapter     mAdapter;
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

        chatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                String msg = etMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    mAdapter.addData(msg);
                    recyclerview.scrollToPosition(mAdapter.getItemCount() - 1);
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

            @Override
            public void onNoPermission(String permission) {//没权限
                super.onNoPermission(permission);
                chatLayout.showPermissionDialog();
            }

            @Override
            public void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs) {
                super.onVoiceRecordSuccess(audioPath, durationMs);
                mAdapter.addData(String.format(Locale.getDefault(), "audioPath=%s, " +
                        "durationMs=%d", audioPath, durationMs));
                recyclerview.scrollToPosition(mAdapter.getItemCount() - 1);
            }

            @Override
            public void onVoiceRecordError(Exception e) {//录音失败
                super.onVoiceRecordError(e);
            }

            //还可重写其它方法override other method ...
        });
        mAdapter = new ChatListAdapter(items);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                toast(items.get(position));
            }
        });
        recyclerview.setAdapter(mAdapter);
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
            FaceManager.handlerEmojiText(tv, item);
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
