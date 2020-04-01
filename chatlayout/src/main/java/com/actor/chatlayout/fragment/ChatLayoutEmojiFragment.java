package com.actor.chatlayout.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actor.chatlayout.R;
import com.actor.chatlayout.bean.Emoji;
import com.actor.chatlayout.bean.FaceGroup;
import com.actor.chatlayout.utils.ConverUtils;
import com.actor.chatlayout.utils.FaceManager;
import com.actor.chatlayout.utils.RecentEmojiManager;
import com.actor.chatlayout.weight.FaceGroupIcon;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: 表情
 * Date       : 2019/6/2 on 20:08
 */
public class ChatLayoutEmojiFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private FaceGroupIcon      faceFirstSetTv;
    private FaceGroupIcon      mCurrentSelected;
    private LinearLayout       faceGroup;
    private int mCurrentGroupIndex = 0;
    private int dp10;

    private ArrayList<View>      ViewPagerItems = new ArrayList<>();
    private List<Emoji>     emojiList;
    private List<FaceGroup> customFaces;
    private OnEmojiClickListener emojiClickListener;

    //最近使用表情
    private RecentEmojiManager   recentManager;
    private List<Emoji>     recentlyEmojiList;

    public ChatLayoutEmojiFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        recentManager = RecentEmojiManager.make(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emojiList = FaceManager.getDefaultEmojiList();
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
        faceFirstSetTv = view.findViewById(R.id.face_first_set);
        faceGroup = view.findViewById(R.id.face_view_group);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dp10 = ConverUtils.dp2px(10);
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.addItemDecoration(new BaseItemDecoration(dp10, dp10));
        recyclerView.setAdapter(myAdapter);
    }

    //Emoji的Adapter
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_for_chat_layout_emoji_fragment, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Emoji emoji = emojiList.get(position);
            Glide.with(getActivity()).load("file:///android_asset/" + emoji.assetsPath).into(holder.ivFaceView);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if (emojiClickListener != null) emojiClickListener.onEmojiClick(emojiList.get(pos));
                }
            });
        }

        @Override
        public int getItemCount() {
            return emojiList.size();
        }
    }
    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivFaceView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFaceView = itemView.findViewById(R.id.iv_item_for_chat_layout_emoji_fragment);
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
        mCurrentSelected = faceFirstSetTv;
        faceFirstSetTv.setSelected(true);
        faceFirstSetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.face_first_set) {
                    if (mCurrentSelected != v) {
                        mCurrentGroupIndex = 0;
                        mCurrentSelected.setSelected(false);
                        mCurrentSelected = (FaceGroupIcon) v;
                        mCurrentSelected.setSelected(true);
                    }
                }
            }
        });
        customFaces = FaceManager.getCustomFaceList();

        int width = ConverUtils.dp2px(70);
        for (int i = 0; i < customFaces.size(); i++) {
            final FaceGroup group = customFaces.get(i);
            FaceGroupIcon faceBtn = new FaceGroupIcon(getActivity());
            faceBtn.setFaceTabIcon(group.groupIcon);

            faceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentSelected != v) {
                        mCurrentGroupIndex = group.groupId;
                        ArrayList<Emoji> faces = group.faces;
                        mCurrentSelected.setSelected(false);
                        mCurrentSelected = (FaceGroupIcon) v;
                        mCurrentSelected.setSelected(true);
                    }

                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
            faceGroup.addView(faceBtn, params);
        }
    }

    public void setOnEmojiClickListener(OnEmojiClickListener onEmojiClickListener) {
        this.emojiClickListener = onEmojiClickListener;
    }

    public interface OnEmojiClickListener {
        void onEmojiDelete();

        void onEmojiClick(Emoji emoji);

        void onCustomFaceClick(int groupIndex, Emoji emoji);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        emojiList = null;
        recentlyEmojiList = null;
        if (ViewPagerItems != null) ViewPagerItems.clear();
        ViewPagerItems = null;
        customFaces = null;
        emojiClickListener = null;
    }
}
