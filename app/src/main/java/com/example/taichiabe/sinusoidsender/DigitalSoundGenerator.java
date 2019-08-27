package com.example.taichiabe.sinusoidsender;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class DigitalSoundGenerator {

    private AudioTrack audioTrack;

    // サンプリング周波数
    private int sampleRate;
    // バッファ・サイズ
    private int bufferSize;

    /**
     * コンストラクタ
     */
    public DigitalSoundGenerator(int sampleRate, int bufferSize) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;

        // AudioTrackを作成
        this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,  // 音楽ストリームを設定
                sampleRate, // サンプルレート
                AudioFormat.CHANNEL_OUT_MONO, // モノラル
                AudioFormat.ENCODING_PCM_16BIT,   // オーディオデータフォーマットPCM16とかPCM8とか
                bufferSize, // バッファ・サイズ
                AudioTrack.MODE_STREAM); // Streamモード。データを書きながら再生する
    }

    /**
     * 正弦波生成
     * @param frequency 送信周波数
     * @return 音声データ
     */
    public short[] getSoundShort(double frequency) {
        //frequency = frequency / 2;
        double[] value = new double[sampleRate];
        double max = 0.0;
        for(int i = 0; i < sampleRate; i++) {
            value[i] = Math.sin(2.0 * Math.PI * frequency * i / sampleRate);
            if(value[i] > max) {
                max = value[i];
            }
        }
        short[] buffer = toShort(value, max);

        return buffer;
    }

    //double型をshort型に変換 (max = 32767 で正規化)
    public short[] toShort(double[] val, double max) {
        double trans = 32767 / max;
        short[] buf = new short[sampleRate];
        for(int i = 0; i < sampleRate; i++) {
            buf[i] = (short)Math.round(val[i] * trans);
        }
        return buf;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }
}

