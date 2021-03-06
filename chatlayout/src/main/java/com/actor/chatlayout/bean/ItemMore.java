package com.actor.chatlayout.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description: MoreFragment 中的item
 * Author     : 李大发
 * Date       : 2019/6/2 on 21:33
 */
public class ItemMore implements Parcelable {

    public int itemIcon;//Item 的 resId
    public String itemIconUrl;//Item图标地址
    public String itemText;//Item名称

    /**
     * @param itemIconUrl 图标地址
     * @param itemText 文字
     */
    public ItemMore(String itemIconUrl, String itemText) {
        this.itemIconUrl = itemIconUrl;
        this.itemText = itemText;
    }

    /**
     * @param itemIcon 图标
     * @param itemText 文字
     */
    public ItemMore(int itemIcon, String itemText) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
    }

    protected ItemMore(Parcel in) {
        itemIcon = in.readInt();
        itemIconUrl = in.readString();
        itemText = in.readString();
    }

    //必须要有一个非空的静态变量 CREATOR
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemIcon);
        dest.writeString(itemIconUrl);
        dest.writeString(itemText);
    }
}
