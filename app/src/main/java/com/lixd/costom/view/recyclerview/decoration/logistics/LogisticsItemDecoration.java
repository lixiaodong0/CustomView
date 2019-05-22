package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lixd.costom.view.R;
import com.lixd.costom.view.utils.UnitUtils;

/**
 * 物流进度装饰
 */
public class LogisticsItemDecoration extends RecyclerView.ItemDecoration {
    //间距的宽度 大圆圈和左右文字的间距
    private static final int LEFT_RIGHT_SPACE_WIDTH = UnitUtils.dp2px(10);
    //item左边空出来的最大间距
    private static final int MAX_LEFT_WIDTH = UnitUtils.dp2px(70);
    //3分之1的宽度 - 左右两边的边距
    private static final int MAX_ICON_WIDTH = MAX_LEFT_WIDTH / 3;
    //3分之2的宽度
    private static final int MAX_TEXT_WIDTH = MAX_LEFT_WIDTH / 3 * 2;
    //大圆的大小
    private static final int BIG_CIRCLE_SIZE = MAX_LEFT_WIDTH / 3;
    //小圆的大小
    private static final int SMALL_CIRCLE_SIZE = UnitUtils.dp2px(5);

    //文本默认的颜色
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#8a8a8a");
    //文本选中的颜色
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.parseColor("#000000");

    //文本默认的大小
    private static final float DEFAULT_TEXT_SIZE = UnitUtils.sp2px(10);
    //文本选中的大小
    private static final float DEFAULT_SELECTED_TEXT_SIZE = UnitUtils.sp2px(14);

    //大小圆默认的颜色
    private static final int DEFAULT_CIRCLE_COLOR = Color.parseColor("#8a8a8a");
    //大小圆选中的颜色
    private static final int DEFAULT_SELECTED_CIRCLE_COLOR = Color.parseColor("#000000");

    //上下线条默认的颜色
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#8a8a8a");
    //上下线条选中的颜色
    private static final int DEFAULT_SELECTED_LINE_COLOR = Color.parseColor("#000000");

    //绘制文本的画笔
    private Paint mTextPaint;
    //大圆的画笔
    private Paint mBigCirclePaint;
    //小圆的画笔
    private Paint mSmallCirclePaint;
    //绘制线条的画笔
    private Paint mLinePaint;
    //上下文
    private Context mContext;

    public LogisticsItemDecoration(Context context) {
        mContext = context;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setColor(DEFAULT_TEXT_COLOR);

        mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigCirclePaint.setStyle(Paint.Style.STROKE);
        mBigCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);
        mBigCirclePaint.setStrokeWidth(UnitUtils.dp2px(1));

        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(UnitUtils.dp2px(1));
        mLinePaint.setColor(DEFAULT_LINE_COLOR);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        LogisticsAdapter adapter = (LogisticsAdapter) parent.getAdapter();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            //根据View取出对应的adapterPosition 切勿用变量i,否则会导致计算高度不准确,因为有RecyclerView有缓存机制
            int childAdapterPosition = parent.getChildAdapterPosition(child);
            //获取数据源
            LogisticsBean data = adapter.getData(childAdapterPosition);
            //获取上一个数据源,用于绘制上下两个线条的时候,如果上一个节点是选中的,上边的线条颜色必须是选中的
            LogisticsBean lastData = null;
            try {
                lastData = adapter.getData(childAdapterPosition - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            //绘制方法
            draw(c, child, data, lastData);
        }
    }

    /**
     * 总的绘制方法
     *
     * @param c        画板
     * @param child    当前条目的根布局对象
     * @param data     当前数据源
     * @param lastData 上一个数据源
     */
    private void draw(Canvas c, View child, LogisticsBean data, LogisticsBean lastData) {
        //根据当前状态 更新画笔的颜色
        if (data.node.isSelected) {
            mTextPaint.setColor(DEFAULT_SELECTED_TEXT_COLOR);
            mLinePaint.setColor(DEFAULT_SELECTED_LINE_COLOR);
            mBigCirclePaint.setColor(DEFAULT_SELECTED_CIRCLE_COLOR);
            mSmallCirclePaint.setColor(DEFAULT_SELECTED_CIRCLE_COLOR);
        } else {
            mTextPaint.setColor(DEFAULT_TEXT_COLOR);
            mLinePaint.setColor(DEFAULT_LINE_COLOR);
            mBigCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);
            mSmallCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);
        }

        int left = child.getLeft();
        //找出画圆的中心x轴  left - 一个Rig
        float cx = left - LEFT_RIGHT_SPACE_WIDTH - MAX_ICON_WIDTH / 2;
        //找出控件,方便获取控件高度
        View tvTitle = child.findViewById(R.id.tv_title);
        View tvDetail = child.findViewById(R.id.tv_detail);

        int height = data.node.iconRes != 0 ? tvTitle.getHeight() : tvDetail.getHeight();
        //绘制图片
        drawIconLayout(c, cx, child, height, data.node, lastData == null ? null : lastData.node);
        //绘制文本
        drawDateText(c, child, height, data);
    }

    /**
     * 绘制圆形和图片
     *
     * @param c
     * @param cx
     * @param child
     * @param height
     * @param node
     */
    private void drawIconLayout(Canvas c, float cx, View child, int height, LogisticsBean.Node node, LogisticsBean.Node lastNode) {
        int cy = 0;
        if (node.iconRes != 0) {
            cy = child.getTop() + height / 2;
            //绘制承载图片的圆形
            c.drawCircle(cx, cy, BIG_CIRCLE_SIZE / 2, mBigCirclePaint);
            //绘制图片
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), node.iconRes);
                if (bitmap != null) {
                    //                    c.drawBitmap(bitmap,);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //没有图片 绘制小圆点
            cy = child.getTop() + height / 2;
            c.drawCircle(cx, cy, SMALL_CIRCLE_SIZE / 2, mSmallCirclePaint);
        }
        //绘制上下两条连接的线条
        drawLine(c, child, cx, cy, node, lastNode);
    }

    private void drawLine(Canvas c, View child, float cx, float cy, LogisticsBean.Node node, LogisticsBean.Node lastNode) {
        boolean isDrawIcon = node.iconRes != 0;
        //绘制底部的线条
        float startX = cx;
        float startY = cy + (isDrawIcon ? BIG_CIRCLE_SIZE / 2 : SMALL_CIRCLE_SIZE / 2);
        if (!isDrawIcon) {
            startY += 10;
        }
        if (node.isNextNode) {
            c.drawLine(startX, startY, startX, child.getBottom(), mLinePaint);
        }
        //绘制顶部的线条
        if (lastNode != null && lastNode.isSelected) {
            mLinePaint.setColor(DEFAULT_SELECTED_LINE_COLOR);
        } else {
            mLinePaint.setColor(DEFAULT_LINE_COLOR);
        }

        float endY = cy - (isDrawIcon ? BIG_CIRCLE_SIZE / 2 : SMALL_CIRCLE_SIZE / 2);
        if (!isDrawIcon) {
            endY -= 10;
        }
        if (!node.isFinish) {
            c.drawLine(startX, child.getTop(), startX, endY, mLinePaint);
        }
    }


    private void drawDateText(Canvas c, View child, int height, LogisticsBean data) {
        int cy = child.getTop() + height / 2;

        if (data.node.isSelected) {
            mTextPaint.setTextSize(DEFAULT_SELECTED_TEXT_SIZE);
        } else {
            mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        }

        String date = data.date;
        String time = data.time;
        Rect textRect = new Rect();
        mTextPaint.getTextBounds(date, 0, date.length(), textRect);
        int x = MAX_TEXT_WIDTH - textRect.width();
        int y = cy;
        c.drawText(date, x, y, mTextPaint);

        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        textRect = new Rect();
        mTextPaint.getTextBounds(time, 0, time.length(), textRect);
        x = MAX_TEXT_WIDTH - textRect.width();
        y += LEFT_RIGHT_SPACE_WIDTH;
        c.drawText(time, x, y, mTextPaint);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = MAX_LEFT_WIDTH + LEFT_RIGHT_SPACE_WIDTH * 2;
    }
}
