package com.chatlayout.example.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.chatlayout.utils.FaceManager;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chatlayout.example.R;

/**
 * description: 聊天列表的Adapter
 *
 * @author : 李大发
 * date       : 2021/5/14 on 18
 * @version 1.0
 */
public class ChatListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ChatListAdapter() {
        super(R.layout.item_chat_contact);
        //item点击
        setOnItemChildClickListener((adapter, view, position) -> ToastUtils.showShort(getItem(position)));
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        TextView tv = helper.addOnClickListener(R.id.tv).getView(R.id.tv);
        FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, item);
    }
}
