package com.lixd.costom.view.comment.audio;

public enum StateType {
    //正常状态
    NORMAL,
    //取消状态
    CANCEL,
    //录制时间过短状态
    TIME_SHORT,
    //录制超时状态,但是也算正常录制
    TIMEOUT
}
