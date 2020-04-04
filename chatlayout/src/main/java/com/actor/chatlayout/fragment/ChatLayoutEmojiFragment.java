package com.actor.chatlayout.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actor.chatlayout.R;
import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.utils.FaceManager;
import com.actor.chatlayout.utils.RecentEmojiManager;
import com.actor.myandroidframework.fragment.ActorBaseFragment;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: 表情
 * Date       : 2019/6/2 on 20:08
 */
public class ChatLayoutEmojiFragment extends ActorBaseFragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private int dp10;

    private ArrayList<View>      viewPagerItems = new ArrayList<>();
    private List<Emoji>          emojiList;
    private OnEmojiClickListener emojiClickListener;

    //最近使用表情
    private RecentEmojiManager   recentManager;
    private List<Emoji>     recentlyEmojiList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        recentManager = RecentEmojiManager.make(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emojiList = FaceManager.getEmojiList();
        try {
            Collection collection = recentManager.getCollection(RecentEmojiManager.PREFERENCE_NAME);
            if (collection != null) {
                recentlyEmojiList = (List<Emoji>) collection;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (recentlyEmojiList == null) recentlyEmojiList = new ArrayList<>();
    }

    public static ChatLayoutEmojiFragment newInstance(/*int spanCount, int itemDecorationPx, ArrayList<ItemMore> items*/) {
        ChatLayoutEmojiFragment fragment = new ChatLayoutEmojiFragment();
//        Bundle args = new Bundle();
//        args.putInt(SPAN_COUNT, spanCount);
//        args.putInt(ITEM_DECORATION, itemDecorationPx);
//        args.putParcelableArrayList(ITEMS, items);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_layout_emoji, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_for_chat_layout_emoji_fragment);
        view.findViewById(R.id.iv_delete_for_chat_layout_emoji_fragment).setOnClickListener(this);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dp10 = ConvertUtils.dp2px(10);
        MyAdapter myAdapter = new MyAdapter();
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (emojiClickListener != null) {
                    emojiClickListener.onEmojiClick(myAdapter.getItem(position));
                }
            }
        });
        recyclerView.addItemDecoration(new BaseItemDecoration(dp10, dp10));
        recyclerView.setAdapter(myAdapter);
    }

    //Emoji的Adapter
    protected class MyAdapter extends BaseQuickAdapter<Emoji, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_for_chat_layout_emoji_fragment, emojiList);
        }
        @Override
        protected void convert(@NonNull BaseViewHolder helper, Emoji item) {
            ImageView ivFaceView = helper.getView(R.id.iv_item_for_chat_layout_emoji_fragment);
            Glide.with(ivFaceView).load(Emoji.ASSETS_PREFIX + item.assetsPath).into(ivFaceView);
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_delete_for_chat_layout_emoji_fragment) {//删除
            if (emojiClickListener != null) emojiClickListener.onEmojiDelete();
        }
    }


    private void initViews() {
//        customFaces = FaceManager.getCustomFaceList();
//
//        int width = ConvertUtils.dp2px(70);
//        for (int i = 0; i < customFaces.size(); i++) {
//            final FaceGroup group = customFaces.get(i);
//            FaceGroupIcon faceBtn = new FaceGroupIcon(getActivity());
//            faceBtn.setFaceTabIcon(group.groupIcon);
//
//            faceBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mCurrentSelected != v) {
//                        mCurrentGroupIndex = group.groupId;
//                        ArrayList<Emoji> faces = group.faces;
//                        mCurrentSelected.setSelected(false);
//                        mCurrentSelected = (FaceGroupIcon) v;
//                        mCurrentSelected.setSelected(true);
//                    }
//
//                }
//            });
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
//            faceGroup.addView(faceBtn, params);
//        }
    }

    public void setOnEmojiClickListener(OnEmojiClickListener onEmojiClickListener) {
        this.emojiClickListener = onEmojiClickListener;
    }

    public interface OnEmojiClickListener {
        void onEmojiDelete();

        void onEmojiClick(Emoji emoji);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        emojiList = null;
        recentlyEmojiList = null;
        if (viewPagerItems != null) viewPagerItems.clear();
        viewPagerItems = null;
//        customFaces = null;
        emojiClickListener = null;
    }
}
