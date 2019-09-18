package com.actor.chatlayout.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.actor.chatlayout.R;

/**
 * Description: 来自腾讯TUIKit Demo
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/3 on 13:34
 */
public class FaceGroupIcon extends RelativeLayout {

    private ImageView faceTabIcon;

    public FaceGroupIcon(Context context) {
        super(context);
        init();
    }

    public FaceGroupIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceGroupIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.face_group_icon, this);
        faceTabIcon = findViewById(R.id.face_group_tab_icon);
    }

    public void setFaceTabIcon(Bitmap bitmap) {
        faceTabIcon.setImageBitmap(bitmap);
    }
}
