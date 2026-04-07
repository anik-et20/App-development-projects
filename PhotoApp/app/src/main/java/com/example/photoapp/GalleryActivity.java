package com.example.photoapp;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Uri> imageList;
    TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        textEmpty = findViewById(R.id.textEmpty);
        recyclerView = findViewById(R.id.recyclerView);

        // Grid layout with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Add spacing between items
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(8, 8, 8, 8);
            }
        });

        imageList = new ArrayList<>();

        // Get selected folder URI
        Uri folderUri = getIntent().getParcelableExtra("folderUri");

        loadImages(folderUri);

        recyclerView.setAdapter(new ImageAdapter(this, imageList));

        // Show images or empty message
        if(imageList.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        }
    }

    // Load images from selected folder
    private void loadImages(Uri folderUri) {
        if(folderUri == null) return;

        DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);
        if(folder == null) return;

        for(DocumentFile file : folder.listFiles()){
            if(file.isFile() && file.getType() != null && file.getType().startsWith("image/")){
                imageList.add(file.getUri());
            }
        }
    }
}