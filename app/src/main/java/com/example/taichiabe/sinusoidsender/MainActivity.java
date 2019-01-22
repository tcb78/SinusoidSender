package com.example.taichiabe.sinusoidsender;
/*============================================================*
 * システム：正弦波送信処理
 *============================================================*/
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    //音を何秒鳴らすか
    public static final double WHOLE_NOTE = 1.0;

    //信号音の周波数
    public static final double SIGNAL = 20000;
    public static final double START = 1000;

    int SENDFREQ;
    int VOLUME;
    int AMPLITUDE;
    int SendSR = 44100;
    int SendBufSize = 44100;
    int musicVolume = 0;

    AudioManager audioManager;
    AudioTrack audioTrack = null;
    private List<SoundDto> soundList = new ArrayList<SoundDto>();
    Thread send;
    boolean bIsPlaying = false;

    // Sound生成クラス
    DigitalSoundGenerator soundGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView SendfreqText = findViewById(R.id.SendfreqText);
        SendfreqText.setText(R.string.SendfreqText);
        TextView volumeText = findViewById(R.id.volumeText);
        volumeText.setText(R.string.volumeText);
        TextView amplitudeText = findViewById(R.id.amplitudeText);
        amplitudeText.setText(R.string.amplitudeText);
        Switch switch1 = findViewById(R.id.Switch);
        switch1.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            bIsPlaying = false;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            audioTrack.release();
            bIsPlaying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked) {

            EditText SendfreqEdit = findViewById(R.id.SendfreqEdit);
            EditText volumeEdit = findViewById(R.id.volumeEdit);
            EditText amplitudeEdit = findViewById(R.id.amplitudeEdit);

            SENDFREQ = Integer.parseInt(SendfreqEdit.getText().toString());
            VOLUME = Integer.parseInt(volumeEdit.getText().toString());
            AMPLITUDE = Integer.parseInt(amplitudeEdit.getText().toString());

            //SendSR = 4 * SENDFREQ;
            //SendBufSize = 4 * SENDFREQ;

            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            // SoundGeneratorクラスをサンプルレート44100で作成
            soundGenerator = new DigitalSoundGenerator(44100, 44100);

            // 再生用AudioTrackは、同じサンプルレートで初期化したものを利用する
            audioTrack = soundGenerator.getAudioTrack();

            soundList.add(new SoundDto(generateSound(soundGenerator, SENDFREQ, AMPLITUDE), WHOLE_NOTE));

            musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, VOLUME, 0);

            audioTrack.play();
            bIsPlaying = true;
            send = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(bIsPlaying) {
                        for(SoundDto sound : soundList) {
                            audioTrack.write(sound.getSound(), 0, sound.getSound().length);
                        }
                        Log.d("audioTrack","audioTrack");
                    }
                    audioTrack.stop();
                    audioTrack.release();
                }
            });
            send.start();

        } else {

            if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                //audioTrack.stop();
                //audioTrack.release();
                bIsPlaying = false;
            }
            soundList.clear();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);

        }

    }

    /**
     * ８ビットのピコピコ音を生成する.
     * @param gen Generator
     * @param freq 周波数(音階)
     * @return 音データ
     */
    public byte[] generateSound(DigitalSoundGenerator gen, double freq, double amp) {
        return gen.getSound(freq, amp);
    }
}