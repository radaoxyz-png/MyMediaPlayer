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
    private TextView songText;
    private TextView currentTimeText;
    private TextView totalTimeText;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songText = findViewById(R.id.songText);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);

        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        currentTimeText = findViewById(R.id.currentTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);

        songText.setText("Sample Music");

        mediaPlayer = MediaPlayer.create(this, R.raw.sample);

        if (mediaPlayer != null) {

            seekBar.setMax(mediaPlayer.getDuration());

            totalTimeText.setText(
                    formatTime(mediaPlayer.getDuration())
            );
        }

        // PLAY / PAUSE
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

        // SEEK BAR
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar bar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser && mediaPlayer != null) {

                            mediaPlayer.seekTo(progress);

                            currentTimeText.setText(
                                    formatTime(progress)
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar bar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar bar) {
                    }
                }
        );

        // PREVIOUS
        previousButton.setOnClickListener(v -> {

            if (mediaPlayer != null) {

                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);

                currentTimeText.setText("0:00");
            }
        });

        // NEXT
        nextButton.setOnClickListener(v -> {

            if (mediaPlayer != null) {

                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);

                currentTimeText.setText("0:00");
            }
        });

        // WHEN SONG FINISHES
        mediaPlayer.setOnCompletionListener(mp -> {

            playButton.setText("▶");

            seekBar.setProgress(0);

            currentTimeText.setText("0:00");
        });
    }

    // UPDATE SEEKBAR
    private void updateSeekBar() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            int position = mediaPlayer.getCurrentPosition();

            seekBar.setProgress(position);

            currentTimeText.setText(
                    formatTime(position)
            );

            handler.postDelayed(
                    this::updateSeekBar,
                    500
            );
        }
    }

    // FORMAT TIME
    private String formatTime(int milliseconds) {

        int totalSeconds = milliseconds / 1000;

        int minutes = totalSeconds / 60;

        int seconds = totalSeconds % 60;

        return String.format(
                "%d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {

            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }
}
