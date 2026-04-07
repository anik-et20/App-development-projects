package com.example.videoaudioplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private TextView audioStatus;
    private EditText urlInput;

    // Audio Picker
    private final ActivityResultLauncher<Intent> audioPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri audioUri = result.getData().getData();
                    if (audioUri != null) {
                        String fileName = audioUri.getLastPathSegment();
                        audioStatus.setText(getString(R.string.status_loaded, fileName));
                        setupAudioPlayer(audioUri);
                    }
                }
            });

    // Video Picker
    private final ActivityResultLauncher<Intent> videoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) {
                        playVideo(videoUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioStatus = findViewById(R.id.audioStatus);
        videoView = findViewById(R.id.videoView);
        urlInput = findViewById(R.id.urlInput);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Setup VideoView listeners
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            videoView.start();
            Toast.makeText(MainActivity.this, "Video started", Toast.LENGTH_SHORT).show();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainActivity.this, "Error playing video: " + what, Toast.LENGTH_SHORT).show();
            return true;
        });

        // AUDIO BUTTONS
        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            audioPickerLauncher.launch(intent);
        });

        findViewById(R.id.btnPlayAudio).setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                audioStatus.setText(R.string.status_playing);
            }
        });

        findViewById(R.id.btnPauseAudio).setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                audioStatus.setText(R.string.status_paused);
            }
        });

        findViewById(R.id.btnStopAudio).setOnClickListener(v -> stopAudio());

        // VIDEO BUTTONS
        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Enter URL first", Toast.LENGTH_SHORT).show();
                return;
            }
            playVideo(Uri.parse(url));
        });

        findViewById(R.id.btnSelectVideo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            videoPickerLauncher.launch(intent);
        });

        findViewById(R.id.btnPauseVideo).setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        });

        findViewById(R.id.btnStopVideo).setOnClickListener(v -> videoView.stopPlayback());

        findViewById(R.id.btnRestartVideo).setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.start();
        });
    }

    private void playVideo(Uri uri) {
        try {
            videoView.setVideoURI(uri);
            videoView.requestFocus();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAudioPlayer(Uri uri) {
        stopAudio();
        mediaPlayer = MediaPlayer.create(this, uri);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            audioStatus.setText(R.string.status_playing);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            audioStatus.setText(R.string.status_stopped);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudio();
    }
}