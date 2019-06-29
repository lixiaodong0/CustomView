package com.lixd.costom.view.comment.input;

import java.io.File;

/**
 * 类名:OnInputFinishListener
 * 功能:输入完成监听器
 */
public interface OnInputFinishListener {

    /**
     * 当音频录制成功的回调
     *
     * @param recordAudioFile 音频的文件
     * @param recordDuration  音频的时长
     */
    void onAudioFinish(File recordAudioFile, long recordDuration);

    /**
     * 当音频录制错误的回调
     */
    void onAudioError(String errMsg);

    /**
     * 当文本输入完成的回调
     */
    void onTextFinish(String text);


    /**
     * 空实现的适配器
     */
    class Adapter implements OnInputFinishListener {

        @Override
        public void onAudioFinish(File recordAudioFile, long recordDuration) {

        }

        @Override
        public void onAudioError(String errMsg) {

        }

        @Override
        public void onTextFinish(String text) {

        }
    }
}
