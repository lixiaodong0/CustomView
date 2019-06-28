package com.lixd.costom.view.comment;

import java.io.File;

/**
 * 类名:RecordAudioListener
 * 功能:录制成功的监听器
 */
public interface RecordAudioListener {

    /**
     * 录制成功的回调
     *
     * @param recordAudioFile 录制成功的音频文件
     */
    void onSuccess(File recordAudioFile);

    /**
     * 录制出现错误的回调
     *
     * @param errMsg 错误信息
     */
    void onError(String errMsg);
}
