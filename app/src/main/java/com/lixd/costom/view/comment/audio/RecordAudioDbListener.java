package com.lixd.costom.view.comment.audio;

/**
 * 类名:RecordAudioDbListener
 * 功能:录制音量分贝监听器
 * 参考文章:https://www.jb51.net/article/64806.htm
 */
public interface RecordAudioDbListener {
    /**
     * 当音量分贝发生改变
     *
     * @param decibel
     */
    void onChange(double decibel);
}
