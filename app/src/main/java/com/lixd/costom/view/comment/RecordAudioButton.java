package com.lixd.costom.view.comment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;

/**
 * 类名:AudioButton
 * 功能:录制音频的按钮
 * 录制音频的格式: 优选WAV格式, 备用mp3格式
 */
public class RecordAudioButton extends AppCompatButton {
    private static final String TAG = "RecordAudioButton";
    //判断长按条件时间范围 500毫秒
    private static final int LONG_CLICK_TIME = 500;
    //最小录制的有效时间为1秒
    private static final long MIN_RECORD_TIME = 1000;
    //最大录制的有效时间为60秒
    private static final long MAX_RECORD_TIME = 6000;
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

    /**
     * 处理事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) Math.abs(event.getX());
        int y = (int) Math.abs(event.getY());
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
                    Log.e(TAG, "mStartRecordTime=" + mStartRecordTime);
                }

                if (isRecord(x, y)) {
                    //正常录制提示文本
                    setText("松开结束");
                    if (isReady) {
                        //处于长按才弹窗
                        mDialog.showDialog(StateType.NORMAL);
                    }
                } else {
                    //手指移动超出范围,取消录制文本
                    setText("松开手指，取消发送");
                    if (isReady) {
                        //处于长按才弹窗
                        mDialog.showDialog(StateType.CANCEL);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                setText("按住 说话");
                Log.e(TAG, "ACTION_UP_" + isReady);
                if (isReady) {
                    long endRecordTime = System.currentTimeMillis();
                    Log.e(TAG, "录制时间为:" + (endRecordTime - mStartRecordTime) + "ms");
                    if (endRecordTime - MIN_RECORD_TIME <= mStartRecordTime) {
                        //录制时间过短,这次录制的音频无效
                        mDialog.showDialog(StateType.TIME_SHORT);
                        Log.e(TAG, "录制时间过短");
                    } else {
                        //录制成功
                        Log.e(TAG, "录制成功");
                    }
                }
                reset();
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    /**
     * 检查权限
     *
     * @return
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
                        android.Manifest.permission.RECORD_AUDIO}, 1);
            }
            return false;
        } else {
            //权限
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
        //关闭弹窗
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialog.closeDialog();
            }
        }, 100);
        mDownTime = 0;
        mStartRecordTime = 0;
        isReady = false;
    }

    public interface OnAudioListener {
        void finish(File file);

        void cancel();
    }

}
