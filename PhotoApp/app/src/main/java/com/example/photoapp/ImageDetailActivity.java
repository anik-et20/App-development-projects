package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Uri imageUri;
    Button deletebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        deletebtn = findViewById(R.id.deletebtn);

        String uriString = getIntent().getStringExtra("imageUri");
        imageUri = Uri.parse(uriString);

        imageView.setImageURI(imageUri);

        showDetails();

        deletebtn.setOnClickListener(v -> showDeleteDialog());
    }

    // 🔴 Confirmation Dialog
    private void showDeleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", (dialog, which) -> deleteImage())
                .setNegativeButton("No", null)
                .show();
    }

    // 🗑 Delete Image
    private void deleteImage(){
        try {
            getContentResolver().delete(imageUri, null, null);
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error deleting image", Toast.LENGTH_SHORT).show();
        }
    }

    // 📄 Show Image Details
    private void showDetails() {

        String name = "Unknown";
        String size = "Unknown";
        String date = "Unknown";
        String path = imageUri.toString();

        // 🔹 Get name & size using cursor
        Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                name = cursor.getString(nameIndex);
            }

            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex != -1) {
                long fileSize = cursor.getLong(sizeIndex);
                size = (fileSize / 1024) + " KB"; // better format
            }

            cursor.close();
        }

        // 🔹 Get date using DocumentFile
        DocumentFile file = DocumentFile.fromSingleUri(this, imageUri);
        if (file != null) {
            long lastModified = file.lastModified();

            if (lastModified != 0) {
                date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                        .format(new java.util.Date(lastModified));
            }
        }

        // 🔹 Final details text
        String details = "Name: " + name +
                "\nPath: " + path +
                "\nSize: " + size +
                "\nDate: " + date;

        textView.setText(details);
    }
}