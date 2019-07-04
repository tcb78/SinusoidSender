package com.example.taichiabe.sinusoidsender;
/*============================================================*
 * システム：正弦波送信処理
 *============================================================*/
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    public static int DEFAULT_VOLUME = 0;

    public static final int SAMPLING_RATE = 44100;
    public static final int BUFFER_SIZE = 44100;

    AudioManager audioManager;
    AudioTrack audioTrack = null;
    private List<SoundDto> soundList = new ArrayList<>();
    Thread send;
    boolean isPlaying = false;

    // Sound生成クラス
    DigitalSoundGenerator soundGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView sendingFreqText = findViewById(R.id.sendingFreqText);
        sendingFreqText.setText(R.string.sendingFreqText);
        TextView volumeText = findViewById(R.id.volumeText);
        volumeText.setText(R.string.volumeText);
        TextView amplitudeText = findViewById(R.id.amplitudeText);
        amplitudeText.setText(R.string.amplitudeText);
        Switch sendingSwitch = findViewById(R.id.Switch);
        sendingSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked) {

            EditText sendingFreqEdit = findViewById(R.id.sendingFreqEdit);
            EditText volumeEdit = findViewById(R.id.volumeEdit);
            EditText amplitudeEdit = findViewById(R.id.amplitudeEdit);

            final int SENDING_FREQ = Integer.parseInt(sendingFreqEdit.getText().toString());
            final int SENDING_VOLUME = Integer.parseInt(volumeEdit.getText().toString());
            final int AMPLITUDE = Integer.parseInt(amplitudeEdit.getText().toString());

            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            // SoundGeneratorクラスをサンプルレート44100で作成
            soundGenerator = new DigitalSoundGenerator(SAMPLING_RATE, BUFFER_SIZE);

            // 再生用AudioTrackは、同じサンプルレートで初期化したものを利用する
            audioTrack = soundGenerator.getAudioTrack();

            soundList.add(new SoundDto(generateSound(soundGenerator, SENDING_FREQ, AMPLITUDE)));

            DEFAULT_VOLUME = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SENDING_VOLUME, 0);

            audioTrack.play();
            isPlaying = true;
            send = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(isPlaying) {
                        for(SoundDto sound : soundList) {
                            audioTrack.write(sound.getSound(), 0, sound.getSound().length);
                        }
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
                isPlaying = false;
            }
            soundList.clear();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DEFAULT_VOLUME, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            isPlaying = false;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DEFAULT_VOLUME, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            audioTrack.release();
            isPlaying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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