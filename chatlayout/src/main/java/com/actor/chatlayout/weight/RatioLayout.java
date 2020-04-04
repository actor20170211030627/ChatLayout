package com.actor.chatlayout.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.actor.chatlayout.R;

/**
 * Created by zhengping on 2017/4/6,15:17.
 * 按照比例，动态计算高度的帧布局
 * 自定义属性的使用：
 * 1、给自定义属性起名字
 *      a、自定义属性集合的名称
 *      b、自定义属性的名称
 *              format
 * 2、使用这个自定义属性
 *      a、命名空间
 *      b、自定义属性只能给自定义控件使用
 * 3、在自定义控件中获取自定义属性的值
 *      a、通过attrs，在所有的属性中进行查找
 *      b、通过attrs获取自定义属性集合，然后通过下标索引的方式获取自定义属性的值
 * @deprecated 下一版本更新, 删除
 */

@Deprecated
public class RatioLayout extends FrameLayout {

    private float ratio;        //比例

    //new对象的时候调用
    public RatioLayout(Context context) {
        this(context,null);
    }

    //加载布局的时候
    public RatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs,-1);

    }

    //布局文件中有style的时候
    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //防止用户未定义atrr
        if(attrs != null) {
            //★方式1
            //float ratio = attrs.getAttributeFloatValue(NAMESPACE, "ratio", 0.0f);
            // System.out.println("ratio=" + ratio);

            //★方式2 在所有的属性集合中，获取自定义属性的集合(AttributeSet set, @StyleableRes int[] attrs)
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                    .MyRatioLayoutAttrs);
            //利用系统帮我们生成的一个下标索引   自定义属性集合名称_自定义属性的名称    这个自定义属性位于自定义属性集合中的下标索引
            ratio = typedArray.getFloat(R.styleable.MyRatioLayoutAttrs_ratio, 0.0f);
            typedArray.recycle();//节约内存
        }
    }

    //onMeasure--onLayout--onDraw

    //MeasureSpec:大小+模式
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //整个帧布局的宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //排除左/右边距的影响
        int innerWidthSize = widthSize - getPaddingLeft() - getPaddingRight();

        //模式
        //MeasureSpec.AT_MOST;至多的模式  --》 wrap_content
        //MeasureSpec.EXACTLY 确定的模式  ---> MatchParent  写死dp
        //MeasureSpec.UNSPECIFIED 未确定的模式
        //宽布局的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //比例 != 0;    宽模式 = 确定;    高模式 != 确定;
        if(ratio != 0 && widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {

            //重新计算heightSize
            heightSize = (int) (innerWidthSize/ratio +0.5f);//此时的heightSize我们想把它当作啥？当作图片的高度
            heightSize  = heightSize + getPaddingTop() + getPaddingBottom();

            //setMeasuredDimension(widthSize,heightSize);仅仅只是确定了RatioLayout的大小，但是RatioLayout的孩子没有走measure方法
            //重新生成measureSpec
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
