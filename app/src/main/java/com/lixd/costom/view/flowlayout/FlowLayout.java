package com.lixd.costom.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //FlowLayout总宽度
    private int width;
    //FlowLayout总高度
    private int height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽高的测量的期望值和期望模式
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //防止onMeasure重复测量导致高度计算错误
        height = 0;
        width = 0;
        int lineWidth = 0;
        int lineHeight = 0;

        //获取子View的数量
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //发去子View测量
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //获取子View的测量的宽高
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算子View的margin值
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            childWidth += layoutParams.leftMargin + layoutParams.rightMargin;
            childHeight += layoutParams.topMargin + layoutParams.bottomMargin;


            //当前行宽已经容不下放置控件了,需要换行处理
            if (lineWidth + childWidth > measureWidth) {
                //累计高度
                height += lineHeight;
                //取最大宽度,防止某个View的宽度大于了行宽
                width = Math.max(lineWidth, childWidth);

                //重置高度宽度
                lineHeight = childHeight;
                lineWidth = childWidth;
            } else {
                //累计行宽
                lineWidth += childWidth;
                //取最大高度 防止某个View的高度大于了行高
                lineHeight = Math.max(lineHeight, childHeight);
            }

            //最后一行是不会超出width范围的，所以要单独计算宽高
            if (i == childCount - 1) {
                width = Math.max(lineWidth, childWidth);
                height += lineHeight;
            }
        }
        //通知系统保存测量的值
        setMeasuredDimension(measureWidthMode == MeasureSpec.EXACTLY ? measureWidth : width,
                measureHeightMode == MeasureSpec.EXACTLY ? measureHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
        //计算控件top left的位置
        int top = 0, left = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > getMeasuredWidth()) {
                //换行的情况,需要累加高度
                top += lineHeight;
                //归零处理
                left = 0;

                //初始化
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            //在摆放的时候,需要加入边距处理
            int lc = left + lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();
            child.layout(lc, tc, rc, bc);

            //left的值 下一个控件的起始位置
            left += childWidth;
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
}
