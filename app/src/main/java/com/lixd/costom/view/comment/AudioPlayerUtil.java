package com.lixd.costom.view.comment;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayerUtil {
    private MediaPlayer mMediaPlayer;

    public AudioPlayerUtil() {
        mMediaPlayer = new MediaPlayer();
        //设置音量，参数分别表示左右声道声音大小，取值范围为0~1
        mMediaPlayer.setVolume(0.5f, 0.5f);
        //设置是否循环播放
        mMediaPlayer.setLooping(false);
    }

    public void play(String url) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            //设置准备监听器
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //开始播放
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
