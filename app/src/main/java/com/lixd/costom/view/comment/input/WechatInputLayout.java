package com.lixd.costom.view.comment.input;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixd.costom.view.R;
import com.lixd.costom.view.comment.audio.RecordAudioButton;
import com.lixd.costom.view.comment.audio.RecordAudioListener;

import java.io.File;

/**
 * 类名:WechatInputLayout
 * 功能:防微信输入聊天布局样式
 */
public class WechatInputLayout extends ConstraintLayout {

    private ImageView mImgToggle;
    private RecordAudioButton mRabAudio;
    private EditText mEtContent;

    //是否是语音输入状态
    private boolean isAudioState = false;

    public WechatInputLayout(Context context) {
        this(context, null);
    }

    public WechatInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WechatInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.wechat_input_layout, this);
        initView();
        initEvent();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mImgToggle = findViewById(R.id.img_toggle);
        mRabAudio = findViewById(R.id.rab_audio);
        mEtContent = findViewById(R.id.et_content);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mImgToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudioState = !isAudioState;
                if (isAudioState) {
                    //语音输入模式
                    closeKeyboard();
                    mImgToggle.setImageResource(R.drawable.wechat_input_audio_toggle_selector);
                    mRabAudio.setVisibility(View.VISIBLE);
                    mEtContent.setVisibility(View.GONE);
                } else {
                    //输入法输入模式
                    openKeyboard();
                    mImgToggle.setImageResource(R.drawable.wechat_input_keyboard_toggle_selector);
                    mEtContent.setVisibility(View.VISIBLE);
                    mRabAudio.setVisibility(View.GONE);
                }
            }
        });

        //监听键盘发送按钮
        mEtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String content = mEtContent.getText().toString().trim();
                    //置空文本
                    mEtContent.setText("");
                    closeKeyboard();
                    send(content);
                }
                return false;
            }
        });

        //监听录制音频的事件
        mRabAudio.setRecordAudioListener(new RecordAudioListener() {
            @Override
            public void onSuccess(File recordAudioFile, long recordDuration) {
                if (mOnInputFinishListener != null) {
                    mOnInputFinishListener.onAudioFinish(recordAudioFile, recordDuration);
                }
            }

            @Override
            public void onError(String errMsg) {
                if (mOnInputFinishListener != null) {
                    mOnInputFinishListener.onAudioError(errMsg);
                }
            }
        });
    }

    /**
     * 发送文本
     *
     * @param text
     */
    private void send(String text) {
        if (mOnInputFinishListener != null) {
            mOnInputFinishListener.onTextFinish(text);
        }
    }

    /**
     * 关闭软键盘
     */
    private void closeKeyboard() {
        KeyboardUtils.hideSoftInput(mEtContent);
    }

    /**
     * 打开软键盘
     */
    private void openKeyboard() {
        KeyboardUtils.showSoftInput(mEtContent);
    }

    private OnInputFinishListener mOnInputFinishListener;

    public void setOnInputFinishListener(OnInputFinishListener listener) {
        mOnInputFinishListener = listener;
    }

}
