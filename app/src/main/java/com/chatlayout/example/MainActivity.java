package com.chatlayout.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actor.chatlayout.ChatLayout;
import com.actor.chatlayout.ChatLayoutKit;
import com.actor.chatlayout.OnListener;
import com.actor.chatlayout.VoiceRecorderView;
import com.actor.chatlayout.bean.ItemMore;
import com.actor.chatlayout.fragment.EmojiFragment;
import com.actor.chatlayout.fragment.MoreFragment;
import com.actor.chatlayout.utils.FaceManager;
import com.chatlayout.example.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    private RecyclerView      rvRecyclerview;
    private VoiceRecorderView voiceRecorder;
    private ChatLayout        clChatLayout;
    private RecyclerView      rvBottom;
    private FrameLayout       fl_bottom;

    private ChatListAdapter      chatListAdapter;
    private List<String>         items           = new ArrayList<>();
    private ArrayList<ItemMore> bottomViewDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), 40);
        setContentView(R.layout.activity_main);

        ChatLayoutKit.init(getApplicationContext());//初始化

        rvRecyclerview = findViewById(R.id.rv_recyclerview);
        voiceRecorder = findViewById(R.id.voice_recorder);
        clChatLayout = findViewById(R.id.cl_chatLayout);
//        rvBottom = findViewById(R.id.rv_bottom);
        fl_bottom = findViewById(R.id.fl_bottom);

        for (int i = 0; i < 20; i++) {
            items.add("Hello World!    " + i);
        }
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ItemMore(imgRes, "Item" + i));
        }

        clChatLayout.init(rvRecyclerview, fl_bottom, voiceRecorder);//rvBottom

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        EmojiFragment emojiFragment = new EmojiFragment();

        MoreFragment moreFragment = MoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new MoreFragment.OnItemClickListener() {//更多点击
            @Override
            public void onItemClick(int position, ItemMore itemMore) {
                toast(itemMore.itemText);
            }
        });
        clChatLayout.setBottomFragments(getSupportFragmentManager(), emojiFragment, moreFragment);
        /**
         * 可重写其它方法,详情点击{@link OnListener}
         */
        clChatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                String msg = etMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    addData(msg);
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
                clChatLayout.showPermissionDialog();
            }

            @Override
            public void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs) {
                super.onVoiceRecordSuccess(audioPath, durationMs);
                addData(String.format(Locale.getDefault(), "audioPath=%s, durationMs=%d", audioPath, durationMs));
            }

            @Override
            public void onVoiceRecordError(Exception e) {//录音失败
                super.onVoiceRecordError(e);
            }

            //override other method ...
        });
        chatListAdapter = new ChatListAdapter();
        rvRecyclerview.setAdapter(chatListAdapter);
//        rvBottom.addItemDecoration(new RVItemDecoration(50, 50));//setPadding间距px
    }

    private void addData(String data) {
        items.add(data);
        chatListAdapter.notifyItemInserted(chatListAdapter.getItemCount() - 1);
        rvRecyclerview.scrollToPosition(chatListAdapter.getItemCount() - 1);
    }

    /**
     * 聊天列表的Adapter
     */
    public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
        @Override
        public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_chat_contact, parent, false);
            return new ChatListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatListViewHolder holder, int position) {
            FaceManager.handlerEmojiText(holder.tv, items.get(position));
//            holder.tv.setText(items.get(position));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast(((TextView)v).getText());
                }
            });
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }
    }
    static class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public ChatListViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    private Toast toast;
    private void toast(CharSequence msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else toast.setText(msg);
        toast.show();
    }

    /**
     * 如果BottomView == Gone,才finish()掉activity
     */
    @Override
    public void onBackPressed() {
        if (clChatLayout.isBottomViewGone()) super.onBackPressed();
    }
}
