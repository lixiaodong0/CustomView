package com.lixd.costom.view.comment.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 类名:RecordAudioHelper
 * 功能:录制音频帮助类
 * 音频的格式: WAV格式
 * 参考文章:https://blog.csdn.net/top_code/article/details/44651313
 */
public class RecordAudioHelper {
    private static final String TAG = "RecordAudioHelper";
    private static final int SAMPLE_RATE_IN_HZ = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE_IN_BYTES = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT);
    //音频录制类
    private AudioRecord mAudioRecord;
    //是否录制中
    private boolean isRecording;
    //用于将回调发送到主线程
    private Handler mHandler = null;
    //录制的线程,用于将音频源输出到本地存储卡
    private RecordRunnable mRecordRunnable;

    public RecordAudioHelper() {

        mHandler = new Handler(Looper.getMainLooper());
    }


    public void startRecord(Context context, RecordAudioListener listener) {
        startRecord(context, listener, null);
    }

    /**
     * 开始录制的方法
     *
     * @param listener              录制的监听器
     * @param recordAudioDbListener 录制中分贝的监听器
     */
    public void startRecord(Context context, RecordAudioListener listener, RecordAudioDbListener recordAudioDbListener) {
        if (isRecording()) {
            Log.e(TAG, "录制还未结束,请勿重复录制");
            return;
        }
        //构建录音对象
        /**
         * audioSource:是指录制源在此我们选择麦克风
         * sampleRateInHz:默认采样频率，以赫兹为单位，官方文档说44100 为目前所有设备兼容，但是如果用模拟器测试的话会有问题，所以有的也用8000
         * channelConfig:描述音频通道设置 CHANNEL_IN_MONO保证能在所有设备上工作
         * audioFormat:音频流的格式，分为16bit 或8bit目前都支持的是ENCODING_PCM_16BIT.
         * bufferSizeInBytes:在录制过程中,音频数据写入缓冲区的总数(字节).从缓冲区读取的新音频数据总会小于此值.这个值一般通过getMinBufferSize来获取.getMinBufferSize的参数可以参照audiorecord的构造函数。
         */
        try {
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE_IN_BYTES);
            Log.e(TAG, "开启录制线程.......");
            mAudioRecord.startRecording();
            isRecording = true;
            //生成的文件路径如下: data/user/0/包名/cache/audio/1561711377776.wav
            String fileName = System.currentTimeMillis() + ".wav";
            //        File recordFile = new File(getRecordRootDir(context), fileName);
            File recordFile = new File(getSdRecordRootDir(), fileName);
            mRecordRunnable = new RecordRunnable(recordFile, listener, recordAudioDbListener);
            Thread thread = new Thread(mRecordRunnable);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "录制发生了错误,可能是因为系统默认禁用了录音权限");
            listener.onError("录制发生了错误");
        }
    }

    /**
     * 根据App缓存路径组成录制目录
     * 好处:
     * 1.App缓存路径不需要sd卡读写权限
     * 2.缓存路径的文件不能随意访问
     * 如下:
     * /data/user/0/App包名/cache/audio
     *
     * @param context
     * @return
     */
    private File getRecordRootDir(Context context) {
        return new File(context.getCacheDir(), "audio");
    }

    private File getSdRecordRootDir() {
        return new File(Environment.getExternalStorageDirectory(), "GGN/audio");
    }

    /**
     * 结束录制的方法
     *
     * @param type 录制的状态值,如果是录制时间过短或者取消状态,要删除录制文件
     */
    public void stopRecord(StateType type) {
        if (isRecording()) {
            Log.e(TAG, "关闭录制线程.......");
            mAudioRecord.stop();
            isRecording = false;
            switch (type) {
                case TIME_SHORT:
                case CANCEL:
                    if (mRecordRunnable != null) {
                        mRecordRunnable.setRecordCancel(true);
                    }
                    break;
                case TIMEOUT:
                case NORMAL:
                default:
                    if (mRecordRunnable != null) {
                        mRecordRunnable.setRecordCancel(false);
                    }
                    break;
            }

        }
    }

    /**
     * 是否在录制中
     *
     * @return
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 销毁的方法
     */
    public void destroy() {
        if (isRecording()) {
            stopRecord(StateType.CANCEL);
            //释放资源
            mAudioRecord.release();
            //置空对象
            mAudioRecord = null;

            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }

    /**
     * 录制任务线程
     */
    private class RecordRunnable implements Runnable {
        //录制的存储文件
        private File recordFile;
        //录制监听器
        private RecordAudioListener recordAudioListener;
        private RecordAudioDbListener recordAudioDbListner;
        //取消录制标识符
        private boolean isRecordCancel;

        private RecordRunnable(File recordFile, RecordAudioListener recordAudioListener) {
            this(recordFile, recordAudioListener, null);
        }

        private RecordRunnable(File recordFile, RecordAudioListener recordAudioListener, RecordAudioDbListener recordAudioDbListener) {
            this.recordFile = recordFile;
            this.recordAudioListener = recordAudioListener;
            this.recordAudioDbListner = recordAudioDbListener;
        }

        @Override
        public void run() {
            OutputStream os = null;
            ByteArrayOutputStream baos = null;
            try {
                long startTime = System.currentTimeMillis();
                if (!recordFile.getParentFile().exists()) {
                    recordFile.getParentFile().mkdirs();
                }
                if (!recordFile.exists()) {
                    recordFile.createNewFile();
                }
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE_IN_BYTES];
                int bufferReadResult = 0;
                long lastCalculateDbTime = 0;
                while (isRecording()) {
                    //读取录音数据
                    bufferReadResult = mAudioRecord.read(buffer, 0, buffer.length);

                    //计算分贝
                    if (recordAudioDbListner != null) {
                        long time = System.currentTimeMillis();
                        if (time - lastCalculateDbTime >= 1000) {
                            double db = calculateDb(bufferReadResult, buffer);
//                            doublecalculateVolume(buffer);
                            recordAudioDbListner.onChange(db);
                            lastCalculateDbTime = System.currentTimeMillis();
                        }
                    }

                    //将数据存储到内存中
                    baos.write(buffer, 0, bufferReadResult);
                }

                //将音频文件保存到本地路径
                byte[] dataBytes = baos.toByteArray();
                os = new FileOutputStream(recordFile);
                //输出WAV格式的头信息,如果没有头信息,音频将不能播放
                os.write(outputWavHeaderInfo(dataBytes.length));
                os.write(dataBytes);

                //关闭流
                baos.close();
                os.close();

                //每次录制完毕释放资源
                mAudioRecord.release();

                //计算录制的时长
                long endTime = System.currentTimeMillis();
                final long recordDuration = endTime - startTime;
                if (!isRecordCancel) {
                    //录制成功回调
                    if (recordAudioListener != null) {
                        Log.e(TAG, "录制成功->" + recordFile.getAbsolutePath());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                recordAudioListener.onSuccess(recordFile, recordDuration);
                            }
                        });
                    }
                } else {
                    //如果是取消状态,删除刚刚录制好的文件,减少存储的空间
                    Log.e(TAG, "取消录制,删除录制文件->" + recordFile.getAbsolutePath());
                    deleteRecordFile();
                }
            } catch (IOException e) {
                if (recordAudioListener != null) {
                    Log.e(TAG, "录制发生了错误", e);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordAudioListener.onError("录制错误");
                        }
                    });
                }
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        public void setRecordCancel(boolean recordCancel) {
            isRecordCancel = recordCancel;
        }

        /**
         * 删除录制文件
         */
        private void deleteRecordFile() {
            if (recordFile.exists() && recordFile.isFile()) {
                boolean delete = recordFile.delete();
                Log.e(TAG, "delete=" + delete);
            }
        }

        /**
         * 计算分贝值
         */
        private double calculateDb(int bufferReadResult, byte[] buffer) {
            //r是实际读取的数据长度，一般而言r会小于buffersize
            int r = bufferReadResult;
            long v = 0;
            // 将 buffer 内容取出，进行平方和运算
            for (int i = 0; i < buffer.length; i++) {
                v += buffer[i] * buffer[i];
            }
            // 平方和除以数据总长度，得到音量大小。
            double mean = v / (double) r;
            double volume = 10 * Math.log10(mean);
            Log.d(TAG, "分贝值:" + volume);
            return volume;
        }

        private double doubleCalculateVolume(byte[] buffer) {
            double sumVolume = 0.0;
            double avgVolume = 0.0;
            double volume = 0.0;
            for (int i = 0; i < buffer.length; i += 2) {
                int v1 = buffer[i] & 0xFF;
                int v2 = buffer[i + 1] & 0xFF;

                int temp = v1 + (v2 << 8);// 小端
                if (temp >= 0x8000) {
                    temp = 0xffff - temp;
                }
                sumVolume += Math.abs(temp);
            }
            avgVolume = sumVolume / buffer.length / 2;
            volume = Math.log10(1 + avgVolume) * 10;
            Log.d(TAG, "doubke分贝值:" + volume);
            return volume;
        }


        /**
         * 输出WAV格式的头信息,如果没有头信息,音频不可播放
         *
         * @param totalAudioLen
         * @return
         */
        private byte[] outputWavHeaderInfo(long totalAudioLen) {
            int mChannels = 1;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = SAMPLE_RATE_IN_HZ;
            long byteRate = SAMPLE_RATE_IN_HZ * 2 * mChannels;

            byte[] header = new byte[44];
            header[0] = 'R';  // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f';  // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = 1;  // format = 1
            header[21] = 0;
            header[22] = (byte) mChannels;
            header[23] = 0;
            header[24] = (byte) (longSampleRate & 0xff);
            header[25] = (byte) ((longSampleRate >> 8) & 0xff);
            header[26] = (byte) ((longSampleRate >> 16) & 0xff);
            header[27] = (byte) ((longSampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = (byte) (2 * mChannels);  // block align
            header[33] = 0;
            header[34] = 16;  // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (totalAudioLen & 0xff);
            header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
            header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
            header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

            return header;
        }
    }
}
