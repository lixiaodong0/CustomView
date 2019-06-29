package com.lixd.costom.view.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixd.costom.view.R;

import java.util.HashMap;
import java.util.Map;

public class RecordStateDialog extends Dialog {
    private static final String TAG = "RecordStateDialog";
    //最大音量图片大小
    private static final int MAX_VOLUME_PIC_SIZE = 9;
    private Context mContext;
    private View rootView;
    private TextView tvStateText;
    private ImageView imgState;
    private StateType mCurStateType;
    private int mCountDownValue = 0;
    private Activity mActivity;
    private static Map<Integer, Integer> VOLUME_PIC_MAP;

    private Handler mHandler = new Handler();

    static {
        //初始化图片
        VOLUME_PIC_MAP = new HashMap<>();
        VOLUME_PIC_MAP.put(0, R.mipmap.rc_ic_volume_0);
        VOLUME_PIC_MAP.put(1, R.mipmap.rc_ic_volume_1);
        VOLUME_PIC_MAP.put(2, R.mipmap.rc_ic_volume_2);
        VOLUME_PIC_MAP.put(3, R.mipmap.rc_ic_volume_3);
        VOLUME_PIC_MAP.put(4, R.mipmap.rc_ic_volume_4);
        VOLUME_PIC_MAP.put(5, R.mipmap.rc_ic_volume_5);
        VOLUME_PIC_MAP.put(6, R.mipmap.rc_ic_volume_6);
        VOLUME_PIC_MAP.put(7, R.mipmap.rc_ic_volume_7);
        VOLUME_PIC_MAP.put(8, R.mipmap.rc_ic_volume_8);
    }

    public RecordStateDialog(Context context) {
        this(context, R.style.dialog);
    }

    public RecordStateDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        mActivity = (Activity) mContext;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_record_state_layout, null, false);
        tvStateText = rootView.findViewById(R.id.tv_state_text);
        imgState = rootView.findViewById(R.id.img_state);
        setContentView(rootView);
        refreshState(StateType.NORMAL);
    }

    /**
     * 显示弹窗
     *
     * @param stateType 当前状态值
     */
    public void showDialog(StateType stateType) {
        mHandler.removeCallbacksAndMessages(null);
        refreshState(stateType);
        if (!isShowing() && !mActivity.isFinishing()) {
            show();
        }
    }

    /**
     * 关闭弹窗
     */
    public void closeDialog() {
        if (isShowing()) {
            if (mCurStateType == StateType.TIME_SHORT) {
                //如果是录制时间过短的状态的话,延迟关闭弹窗,保证弹窗文本显示出来
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 100);
            } else {
                dismiss();
            }
        }
        mCountDownValue = 0;
    }

    /**
     * 销毁的方法
     */
    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
        dismiss();
        mHandler = null;
    }


    /**
     * 倒计时值
     *
     * @param countDownValue
     */
    public void setCountDownValue(int countDownValue) {
        mCountDownValue = countDownValue;
    }

    /**
     * 刷新状态
     *
     * @param stateType
     */
    private void refreshState(StateType stateType) {
        mCurStateType = stateType;
        switch (stateType) {
            case CANCEL:
                imgState.setImageResource(R.mipmap.rc_ic_volume_cancel);
                tvStateText.setText("松开手指，取消发送");
                tvStateText.setBackgroundResource(R.drawable.record_dialog_text_bg);
                break;
            case TIMEOUT:
            case NORMAL:
                /**
                 * 本来应该根据当前分贝值展示合适的图片
                 * 但是因为AudioRecord没有获取分贝的方法,故而采用获取系统音量值来展示
                 *
                 * 如果采用MediaRecorder录制音频的话
                 * 可以参考这个文章:https://blog.csdn.net/a1527238987/article/details/80423565
                 */
                imgState.setImageResource(getVolumePic(getCurrentAudioVolumePercent()));
                if (mCountDownValue != 0) {
                    String text = "还可以说" + mCountDownValue + "秒";
                    tvStateText.setText(text);
                } else {
                    tvStateText.setText("手指上滑，取消发送");
                }
                tvStateText.setBackgroundColor(Color.TRANSPARENT);
                break;
            case TIME_SHORT:
                imgState.setImageResource(R.mipmap.rc_ic_volume_wraning);
                tvStateText.setText("录制时间太短");
                tvStateText.setBackgroundColor(Color.TRANSPARENT);
                break;
            default:
                break;
        }
    }

    /**
     * 根据百分比值获取一张音量图片
     *
     * @return
     */
    private int getVolumePic(float percentValue) {
        int index = (int) (MAX_VOLUME_PIC_SIZE * percentValue);
        return VOLUME_PIC_MAP.get(index);
    }


    /**
     * 获取当前系统音量的百分比值
     *
     * @return
     */
    private float getCurrentAudioVolumePercent() {
        float maxValue = getAudioMaxVolumeAValue();
        float currentValue = getAudioCurrentVolumeAValue();
        float percent = currentValue / maxValue;
        return percent;
    }

    /**
     * 通话音量:AudioManager.STREAM_VOICE_CALL
     * 系统音量:AudioManager.STREAM_SYSTEM
     * 铃声音量:AudioManager.STREAM_RING
     * 音乐音量:AudioManager.STREAM_MUSIC
     * 提示声音音量:AudioManager.STREAM_ALARM
     *
     * @return
     */
    private AudioManager getAudioManager() {
        return (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取媒体最大音量值
     *
     * @return
     */
    private int getAudioMaxVolumeAValue() {
        return getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    /**
     * 获取媒体当前音量值
     *
     * @return
     */
    private int getAudioCurrentVolumeAValue() {
        return getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
    }
}
