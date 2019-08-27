package com.example.taichiabe.sinusoidsender;

public class SoundDto {

    // 音声データ
    private short[] sound;
    // 長さ
    private double length;

    /**
     * 引数付きコンストラクタ
     * @param source 音声データ
     * @param length 長さ
     */
    public SoundDto(short[] source, double length) {
        this.sound = source;
        this.length = length;
    }

    public SoundDto(short[] source) {
        this.sound = source;
    }

    public short[] getSound() {
        return sound;
    }
    public void setSound(short[] sound) {
        this.sound = sound;
    }
    public double getLength() {
        return length;
    }
    public void setLength(double length) {
        this.length = length;
    }
}