package com.chatlayout.example.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.chatlayout.utils.FaceManager;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chatlayout.example.R;
import com.chatlayout.example.info.MessageItem;

/**
 * description: 聊天列表的Adapter
 *
 * @author : 李大发
 * date       : 2021/5/14 on 18
 * @version 1.0
 */
public class ChatListAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {

    public ChatListAdapter() {
        super(R.layout.item_chat_contact);
        //item点击
        setOnItemClickListener((adapter, view, position) -> {
            MessageItem item = getItem(position);
            if (item != null) {
                String message = item.message;
                if (message != null) {
                    ToastUtils.showShort(message);
                } else {
                    AudioUtils.getInstance().playRecord(item.audioPath, null);
                }
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MessageItem item) {
        TextView tv = helper.getView(R.id.tv);
        String message = item.message;
        if (message != null) {
            FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, message);
        } else {
            tv.setText(TextUtils2.getStringFormat("audioPath = %s\ndurationMs = %dms", item.audioPath, item.durationMs));
        }
    }
}
