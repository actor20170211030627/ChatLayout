package com.chatlayout.example;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actor.chatlayout.ChatLayout;
import com.actor.chatlayout.OnListener;
import com.actor.chatlayout.RVItemDecoration;
import com.chatlayout.example.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvRecyclerview;
    private ChatLayout clChatLayout;
    private RecyclerView rvBottom;

    private ChatListAdapter chatListAdapter;
    private List<String> mDatas = new ArrayList<>();
    private List<BottomViewItem> bottomViewDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), 40);
        setContentView(R.layout.activity_main);
        rvRecyclerview = (RecyclerView) findViewById(R.id.rv_recyclerview);
        clChatLayout = (ChatLayout) findViewById(R.id.cl_chatLayout);
        rvBottom = (RecyclerView) findViewById(R.id.rv_bottom);

        for (int i = 0; i < 20; i++) {
            mDatas.add("Hello World!    " + i);
        }
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new BottomViewItem(imgRes, "Item" + i));
        }

        clChatLayout.init(rvRecyclerview, rvBottom);
        /**
         * 可重写其它方法,详情点击{@link OnListener}
         */
        clChatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                String msg = etMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    mDatas.add(msg);
                    //有动画效果
                    chatListAdapter.notifyItemInserted(chatListAdapter.getItemCount() - 1);
                    //滑动到底部
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
        chatListAdapter = new ChatListAdapter();
        rvRecyclerview.setAdapter(chatListAdapter);
        rvBottom.addItemDecoration(new RVItemDecoration(50, 50));//setPadding间距px
        rvBottom.setAdapter(new BottomViewAdapter());
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
            holder.tv.setText(mDatas.get(position));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast(((TextView)v).getText());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }
    static class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public ChatListViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    /**
     * BottomView(是一个RecyclerView) 的Adapter
     */
    private class BottomViewAdapter extends RecyclerView.Adapter<BottomViewViewHolder> {
        @Override
        public BottomViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_chat_bottom, parent, false);
            return new BottomViewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BottomViewViewHolder holder, int position) {
            holder.iv.setImageResource(bottomViewDatas.get(position).imgRes);
            holder.tv.setText(bottomViewDatas.get(position).name);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    toast(bottomViewDatas.get(position).name);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bottomViewDatas.size();
        }
    }
    private class BottomViewViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView tv;
        public BottomViewViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
    private class BottomViewItem {
        private int imgRes;
        private String name;
        public BottomViewItem(@DrawableRes int imgRes, String name) {
            this.imgRes = imgRes;
            this.name = name;
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
