package com.lixd.costom.view.comment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.File;

/**
 * 类名:AudioButton
 * 功能:录制音频的按钮
 */
public class RecordAudioButton extends AppCompatButton implements RecordAudioListener {
    private static final String TAG = "RecordAudioButton";
    //判断长按条件时间范围 500毫秒
    private static final int LONG_CLICK_TIME = 500;
    //最短录制的有效时间为1秒
    private static final long MIN_RECORD_TIME = 1000;
    //最长录制的有效时间为60秒
    private static final long MAX_RECORD_TIME = 20000;
    //最大Y轴移动的范围,单位:px
    private static final int MAX_Y_MOVE_OFFSET = 60;

    //手指按下的事件,用于判断长按事件
    private long mDownTime;
    //开始录制的时间
    private long mStartRecordTime;
    //是否准备好录制
    private boolean isReady;
    //是否有录音权限
    private boolean isRecordPermission;
    //录制状态弹窗
    private RecordStateDialog mDialog;
    //是否已经处理过录制结果,主要是为了区分60秒超时结束和正常手指抬起结束
    private boolean isHandlerRecordResult = false;
    //倒计时任务
    private CountDownTimer mCountDownTimer = new CountDownTimer(MAX_RECORD_TIME, MIN_RECORD_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            //毫秒转化秒
            int second = (int) (millisUntilFinished / 1000);
            //仿照微信最后10提醒用户
            if (second <= 10 && second > 0) {
                mDialog.setCountDownValue(second);
                mDialog.showDialog(StateType.TIMEOUT);
            } else if (second <= 0) {
                //60秒倒计时后结束录制
                isHandlerRecordResult = true;
                handlerRecordResult(x, y);
                mDialog.closeDialog();

            }
            Log.e(TAG, "second=" + second);
        }

        @Override
        public void onFinish() {

        }
    };

    /**
     * 该方法在包含当前View的window可见性改变时被调用。
     *
     * @param hasWindowFocus true有焦点  false无焦点
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.e(TAG, "onWindowFocusChanged = " + hasWindowFocus);
        if (!hasWindowFocus) {
            /**
             * 当页面失去焦点的时候,如:
             *     跳转另外一个页面,
             *     由前台转为后台等
             *     页面弹出Dialog等,
             * 我们就认为这一次的录制没有成功,需要取消倒计时任务.
             * 另外失去焦点的时候,如果我们在正在触摸控件,手指抬起的时候,
             * 我们会收到一个Cancel事件,停止录制的操作就在Cancel事件处理了,
             * 这里只需要取消倒计时任务就行.
             */
            if (!mDialog.isShowing()) {  //过滤自身弹窗引起的onWindowFocusChanged变化
                mCountDownTimer.cancel();
            }
        }
    }


    /**
     * 当activity执行onDestroy()方法,会执行这个方法,
     * 并且这个方法只会被执行一次,一般用于销毁操作.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow");
        //销毁录制帮助对象
        RecordAudioHelper.getInstance().destroy();
        //重置
        reset();
        mCountDownTimer = null;
        mDialog = null;
    }

    public RecordAudioButton(Context context) {
        this(context, null);
    }

    public RecordAudioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordAudioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDialog = new RecordStateDialog(context);
        setText("按住 说话");
    }

    //保存手指移动x,y轴,用于倒计时60秒后,结束录制
    private int x;
    private int y;

    /**
     * 处理手指事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        x = (int) Math.abs(event.getX());
        y = (int) Math.abs(event.getY());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                isRecordPermission = checkPermission();
                break;
            case MotionEvent.ACTION_MOVE:
                long endTime = System.currentTimeMillis();

                //开始录制的判断条件= 长按 + 录制权限
                if (endTime - mDownTime >= LONG_CLICK_TIME && !isReady && isRecordPermission) {
                    //长按事件触发
                    isReady = true;
                    mStartRecordTime = System.currentTimeMillis();
                    //开启60秒倒计时
                    mCountDownTimer.start();
                    Log.e(TAG, "录制开始时间：" + mStartRecordTime);
                    //开启录制功能
                    RecordAudioHelper.getInstance().startRecord(getContext(), this);
                }

                if (isRecord(x, y)) {
                    //正常录制提示文本
                    setText("松开结束");
                    if (isReady && !isHandlerRecordResult) {
                        //长按+没有处理过结果=弹窗显示
                        mDialog.showDialog(StateType.NORMAL);
                    }
                } else {
                    //手指移动超出范围,取消录制文本
                    setText("松开手指，取消发送");
                    if (isReady && !isHandlerRecordResult) {
                        //长按+没有处理过结果=弹窗显示
                        mDialog.showDialog(StateType.CANCEL);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                setText("按住 说话");
                //判断是否已经处理过录制结果
                if (!isHandlerRecordResult) {
                    handlerRecordResult(x, y);
                } else {
                    isHandlerRecordResult = false;
                }
                Log.e(TAG, "ACTION_UP");
                //重置数据
                reset();
                break;
            //如果发生取消事件,取消录制
            case MotionEvent.ACTION_CANCEL:
                setText("按住 说话");
                isHandlerRecordResult = false;
                RecordAudioHelper.getInstance().stopRecord(StateType.CANCEL);
                reset();
                Log.e(TAG, "ACTION_CANCEL");
                break;
        }
        //将事件传递给Button
        super.onTouchEvent(event);
        return true;
    }

    /**
     * 处理录制结果
     *
     * @param x x轴坐标
     * @param y y轴坐标
     */
    private void handlerRecordResult(int x, int y) {
        if (isReady) {
            long endRecordTime = System.currentTimeMillis();
            Log.e(TAG, "录制结束时间：" + endRecordTime);
            Log.e(TAG, "录制时长：" + (endRecordTime - mStartRecordTime) + "ms");

            //录制时间过短,这次录制的音频无效
            if (endRecordTime - MIN_RECORD_TIME <= mStartRecordTime) {
                RecordAudioHelper.getInstance().stopRecord(StateType.TIME_SHORT);
            } else if (!isRecord(x, y)) {
                //超出范围录制无效
                RecordAudioHelper.getInstance().stopRecord(StateType.CANCEL);
            } else {
                //录制成功
                //停止录制功能
                RecordAudioHelper.getInstance().stopRecord(StateType.NORMAL);
            }
        }
    }

    /**
     * 检查权限
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            boolean flag = ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.RECORD_AUDIO);
            if (!flag) {
                //永久拒绝了权限
                new AlertDialog.Builder(getContext())
                        .setMessage("需要开启录音权限才能使用此功能")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
                                getContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                //没有拒绝,申请权限
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            return false;
        } else {
            //有权限
            return true;
        }
    }

    /**
     * 根据手指移动距离判断是否可以录制
     *
     * @param x x轴
     * @param y y轴
     * @return true录制 false取消录制
     */
    private boolean isRecord(int x, int y) {
        if (x < 0 || x > getWidth()) {
            //超出宽度范围
            return false;
        } else if (y > (getHeight() + MAX_Y_MOVE_OFFSET)) {
            //超出高度范围
            return false;
        }
        return true;
    }

    /**
     * 重置数据
     */
    private void reset() {
        //停止60秒倒计时
        mCountDownTimer.cancel();
        //关闭弹窗
        mDialog.closeDialog();
        mDownTime = 0;
        mStartRecordTime = 0;
        isReady = false;
    }

    //音频录制成功回调
    @Override
    public void onSuccess(File recordAudioFile) {
        Toast.makeText(getContext(), "录制成功", Toast.LENGTH_SHORT).show();
    }

    //音频录制失败的回调
    @Override
    public void onError(String errMsg) {
        Toast.makeText(getContext(), "录制失败", Toast.LENGTH_SHORT).show();
    }

}
