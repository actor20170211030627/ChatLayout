package com.chatlayout.example.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.widget.chat.utils.FaceManager;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chatlayout.example.R;

import java.util.List;

/**
 * description: 聊天列表的Adapter
 *
 * @author    : ldf
 * date       : 2021/5/14 on 18
 * @version 1.0
 */
public class ChatListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ChatListAdapter(@Nullable List<String> data) {
        super(R.layout.item_chat_contact, data);
        //item点击
        addChildClickViewIds(R.id.tv);
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ToastUtils.showShort(getItem(position));
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        TextView tv = helper.getView(R.id.tv);
        FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, item);
    }
}
