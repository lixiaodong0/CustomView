package com.lixd.costom.view.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixd.costom.view.R;

public class RecordStateDialog extends Dialog {
    private static final String TAG = "RecordStateDialog";
    private Context mContext;
    private View rootView;
    private TextView tvStateText;
    private ImageView imgState;
    private StateType mCurStateType;
    private int mCountDownValue = 0;
    private Activity mActivity;

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
            dismiss();
        }
        mCountDownValue = 0;
        mCurStateType = StateType.NORMAL;
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
                tvStateText.setBackgroundColor(Color.RED);
                break;
            case TIMEOUT:
            case NORMAL:
                imgState.setImageResource(R.mipmap.rc_ic_volume_0);
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
}
