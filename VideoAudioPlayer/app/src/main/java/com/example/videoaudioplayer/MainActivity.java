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

    // Launcher to pick audio file
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

    // Launcher to pick video file
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

        // Attach media controller to VideoView
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // VideoView listeners
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            videoView.start();
            Toast.makeText(MainActivity.this, "Video started", Toast.LENGTH_SHORT).show();
        });

        // Video error listener
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainActivity.this, "Error playing video: " + what, Toast.LENGTH_SHORT).show();
            return true;
        });

        // Pick audio file
        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            audioPickerLauncher.launch(intent);
        });

        //Play audio
        findViewById(R.id.btnPlayAudio).setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                audioStatus.setText(R.string.status_playing);
            }
        });

        //Pause audio
        findViewById(R.id.btnPauseAudio).setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                audioStatus.setText(R.string.status_paused);
            }
        });

        //Stop audio
        findViewById(R.id.btnStopAudio).setOnClickListener(v -> stopAudio());

        // Play video from URL
        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Enter URL first", Toast.LENGTH_SHORT).show();
                return;
            }
            playVideo(Uri.parse(url));
        });

        // Pick video file
        findViewById(R.id.btnSelectVideo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            videoPickerLauncher.launch(intent);
        });

        //Pause video
        findViewById(R.id.btnPauseVideo).setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        });

        //Stop video
        findViewById(R.id.btnStopVideo).setOnClickListener(v -> videoView.stopPlayback());

        //Restart video
        findViewById(R.id.btnRestartVideo).setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.start();
        });
    }
    // Play video from given URI
    private void playVideo(Uri uri) {
        try {
            videoView.setVideoURI(uri);
            videoView.requestFocus();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Setup and start audio player
    private void setupAudioPlayer(Uri uri) {
        stopAudio();
        mediaPlayer = MediaPlayer.create(this, uri);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            audioStatus.setText(R.string.status_playing);
        }
    }
    // Stop and release audio player
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