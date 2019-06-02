package com.actor.chatlayout;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description: MoreFragment 中的item
 * Copyright  : Copyright (c) 2019
 * Author     : actor
 * Date       : 2019/6/2 on 21:33
 */
public class ItemMore implements Parcelable {

    public int itemIcon;
    public String itemText;

    /**
     * @param itemIcon 图标
     * @param itemText 文字
     */
    public ItemMore(int itemIcon, String itemText) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemIcon);
        dest.writeString(itemText);
    }

    public static final Creator<ItemMore> CREATOR = new Creator<ItemMore>() {
        @Override
        public ItemMore createFromParcel(Parcel in) {
            return new ItemMore(in);
        }

        @Override
        public ItemMore[] newArray(int size) {
            return new ItemMore[size];
        }
    };

    protected ItemMore(Parcel in) {
        itemIcon = in.readInt();
        itemText = in.readString();
    }
}
