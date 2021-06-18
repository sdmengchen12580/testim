package com.example.imchat;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 孟晨 on 2018/7/13.
 */

public class RecoderViewShopGroup extends ViewGroup {

    private int childHorizontalSpace;
    private int childVerticalSpace;

    public RecoderViewShopGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.TagsLayoutShop);
        if (attrArray != null) {
            childHorizontalSpace = attrArray.getDimensionPixelSize(R.styleable.TagsLayoutShop_tagHorizontalSpaces, 0);
            childVerticalSpace = attrArray.getDimensionPixelSize(R.styleable.TagsLayoutShop_tagVerticalSpaces, 0);
            //回收
            attrArray.recycle();
        }
    }

    //这个方法主要是用于父容器添加子View时调用,用于生成和此容器类型相匹配的布局参数类。
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    //设置view的测量模式和大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;//容器的总体高度

        int lineWidth = 0;//记录每一行的宽度，width不断取最大宽度
        int lineHeight = 0;//每一行的高度，会累加至height

        //额外值获取
        int count = getChildCount();
        int left = getPaddingLeft();
        int right = getPaddingRight();
        int top = getPaddingTop();

        int lineNum = 1;//几行

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;
            // 测量child
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // child宽度+自定义间隔=实际占宽
            int childWidth = child.getMeasuredWidth() + childHorizontalSpace;
            // child高度+自定义间隔=实际占高（还要考虑child自身设置margin影响）
            int childHeight = child.getMeasuredHeight() + childVerticalSpace;
            // child实际宽高+左右margin
            LayoutParams lp = child.getLayoutParams();
            if (lp != null && lp instanceof MarginLayoutParams) {
                MarginLayoutParams params = (MarginLayoutParams) lp;
                childWidth += params.leftMargin + params.rightMargin;
                childHeight += params.topMargin + params.bottomMargin;
            }
            //直接换行
            lineNum++;
            lineWidth = childWidth; // 记录新行的宽度
            width = Math.max(lineWidth, childWidth); // 取最大的
            height += lineHeight;//总体高度叠加
            lineHeight = childHeight;// 记录新行的高度
            child.setTag(new Location(left, top + height,
                    childWidth + left - childHorizontalSpace, height + childHeight + top - childVerticalSpace));
        }
        width = Math.max(width, lineWidth) + getPaddingLeft() + getPaddingRight();
        sizeHeight += getPaddingTop() + getPaddingBottom();
        height += lineHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;
            //自动退出后的view不要了
            if (child.getTag() == null)
                break;
            Location location = (Location) child.getTag();
            child.layout(location.left, location.top, location.right, location.bottom);
        }
    }

    //清空
    public void clearAllView() {
        removeAllViews();
        invalidate();
    }

    //记录位置
    public class Location {
        public Location(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int left;
        public int top;
        public int right;
        public int bottom;
    }
}
