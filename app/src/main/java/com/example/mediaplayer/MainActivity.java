package com.example.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSION = 100;

    private MediaPlayer mediaPlayer;

    private SeekBar seekBar;
    private Button playButton;

    private TextView songText;
    private TextView currentTimeText;
    private TextView totalTimeText;

    private ListView mediaList;

    private final Handler handler = new Handler();

    private final ArrayList<String> songNames = new ArrayList<>();
    private final ArrayList<Uri> songUris = new ArrayList<>();

    private int currentSong = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        songText = findViewById(R.id.songText);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);

        currentTimeText = findViewById(R.id.currentTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);

        mediaList = findViewById(R.id.mediaList);

        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        if (checkMediaPermission()) {
            loadSongs();
        } else {
            requestMediaPermission();
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

        // PREVIOUS
        previousButton.setOnClickListener(v -> {

            if (songUris.isEmpty()) {
                return;
            }

            if (currentSong > 0) {
                playSong(currentSong - 1);
            }
        });

        // NEXT
        nextButton.setOnClickListener(v -> {

            if (songUris.isEmpty()) {
                return;
            }

            if (currentSong < songUris.size() - 1) {
                playSong(currentSong + 1);
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
    }

    private boolean checkMediaPermission() {

        if (android.os.Build.VERSION.SDK_INT >= 33) {

            return checkSelfPermission(
                    Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED;

        } else {

            return checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestMediaPermission() {

        if (android.os.Build.VERSION.SDK_INT >= 33) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_MEDIA_AUDIO
                    },
                    REQUEST_PERMISSION
            );

        } else {

            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                loadSongs();
            }
        }
    }

    private void loadSongs() {

        songNames.clear();
        songUris.clear();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME
        };

        String selection =
                MediaStore.Audio.Media.IS_MUSIC + " != 0";

        try (Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC"
        )) {

            if (cursor != null) {

                int idColumn = cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Media._ID
                );

                int nameColumn = cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Media.DISPLAY_NAME
                );

                while (cursor.moveToNext()) {

                    long id = cursor.getLong(idColumn);

                    String name = cursor.getString(nameColumn);

                    Uri uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                    );

                    songNames.add(name);
                    songUris.add(uri);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_
