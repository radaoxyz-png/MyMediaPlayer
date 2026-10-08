package com.example.mediaplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Button playButton;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView songText = findViewById(R.id.songText);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        songText.setText("Sample Music");

        mediaPlayer = MediaPlayer.create(this, R.raw.sample);

        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
        }

        playButton.setOnClickListener(v -> {

            if (mediaPlayer == null) {
                return;
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setText("▶");
            } else {
                mediaPlayer.start();
                playButton.setText("⏸");
                updateSeekBar();
            }
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser && mediaPlayer != null) {
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        mediaPlayer.setOnCompletionListener(mp -> {
            playButton.setText("▶");
            seekBar.setProgress(0);
        });

        previousButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
            }
        });
    }

    private void updateSeekBar() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            seekBar.setProgress(mediaPlayer.getCurrentPosition());

            handler.postDelayed(this::updateSeekBar, 500);
        }
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}
