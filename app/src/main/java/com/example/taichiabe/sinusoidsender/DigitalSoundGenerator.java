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
     * サウンド生成
     * @param frequency 鳴らしたい音の周波数
     * @return 音声データ
     */
    public byte[] getSound(double frequency, double amplitude) {
        frequency = frequency / 2;
        // byteバッファを作成
        byte[] buffer = new byte[bufferSize];
        double max = 0;
        double[] t = new double[buffer.length];
        double hz=frequency/this.sampleRate;
        for(int i = 0; i < buffer.length; i++) {
            //t[i]=Math.sin(i*2*Math.PI*hz);
            t[i] = amplitude * Math.sin(i * 2 * Math.PI * hz);
            Log.d("t[i]",String.valueOf(t[i]));
            if(t[i] > max) {
                max = t[i];
            }
        }
        double trans = 127 / max;
        for(int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte)Math.round(t[i]*trans);

        }
        return buffer;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }
}

