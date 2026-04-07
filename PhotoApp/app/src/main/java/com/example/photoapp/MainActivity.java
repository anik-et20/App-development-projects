package com.example.photoapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btngallery, btncamera;
    private Uri imageUri;

    public static final int PERMISSION_CODE = 100;
    public static final int CAMERA_REQUEST = 101;
    public static final int FOLDER_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btngallery = findViewById(R.id.button2);
        btncamera = findViewById(R.id.button);

        checkpermissions();

        // 📸 Camera
        btncamera.setOnClickListener(v -> openCamera());

        // 🖼 Gallery (UPDATED)
        btngallery.setOnClickListener(v -> openFolderPicker());
    }



    // ✅ Permissions
    private void checkpermissions(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_CODE);
        }
    }

    // 📸 Open Camera
    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    // ✅ Handle Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 📁 Folder selected
        if (requestCode == FOLDER_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri folderUri = data.getData();

            // 🔥 VERY IMPORTANT (fix blank screen)
            getContentResolver().takePersistableUriPermission(
                    folderUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("folderUri", folderUri);
            startActivity(intent);
        }

        // 📸 Camera result
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
        }
    }
    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 200);
    }
}