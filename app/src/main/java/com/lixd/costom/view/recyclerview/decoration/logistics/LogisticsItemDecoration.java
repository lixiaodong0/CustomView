package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    //间距的宽度
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

        mTextPaint = new Paint();

        mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigCirclePaint.setStyle(Paint.Style.STROKE);

        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(UnitUtils.dp2px(2));
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
            draw(c, child, adapter.getData(childAdapterPosition));
        }
    }

    private void draw(Canvas c, View child, LogisticsBean data) {
        int left = child.getLeft();
        //计算画圆的中心点
        float cx = left - LEFT_RIGHT_SPACE_WIDTH - MAX_ICON_WIDTH / 2;
        float cy = 0;
        //找出控件,方便获取控件高度
        View tvTitle = child.findViewById(R.id.tv_title);
        View tvDetail = child.findViewById(R.id.tv_detail);
        //绘制图片
        drawIconLayout(c, cx, child, data.node.iconRes != 0 ? tvTitle.getHeight() : tvDetail.getHeight(), data.node);
        //绘制文本
        drawDateText(c, child, data);
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
    private void drawIconLayout(Canvas c, float cx, View child, int height, LogisticsBean.Node node) {
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
        drawLine(c, child, cx, cy, node);
    }

    private void drawLine(Canvas c, View child, float cx, float cy, LogisticsBean.Node node) {
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
        float endY = cy - (isDrawIcon ? BIG_CIRCLE_SIZE / 2 : SMALL_CIRCLE_SIZE / 2);
        if (!isDrawIcon) {
            endY -= 10;
        }
        if (!node.isFinish) {
            c.drawLine(startX, child.getTop(), startX, endY, mLinePaint);
        }
    }


    private void drawDateText(Canvas c, View child, LogisticsBean data) {

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = MAX_LEFT_WIDTH + LEFT_RIGHT_SPACE_WIDTH * 2;
    }
}
