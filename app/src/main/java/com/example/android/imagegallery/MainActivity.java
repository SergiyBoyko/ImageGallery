package com.example.android.imagegallery;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private CustomAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ArrayList<ImageItem> imageItems = loadData();
        ArrayList<ImageProperties> imageProp = loadImageProperties();


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new CustomAdapter(this, R.layout.grid_item_layout, imageProp);
        gridView.setAdapter(gridAdapter);

        setListener();

    }

    private void setListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageProperties imageProp = (ImageProperties) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", imageProp.getTitle());
                intent.putExtra("image", imageProp.getPath());
                intent.putExtra("length", imageProp.getSize());

                //Start details activity
                startActivity(intent);
            }
        });
    }

    private ArrayList<ImageProperties> loadImageProperties() {
        final ArrayList<ImageProperties> imageProperties = new ArrayList<>();
        File path = new File(Environment.getExternalStorageDirectory(), "ImageGallery");
        if (path.exists()) {
            String[] fileNames = path.list();
            for (int i = 0; i < fileNames.length; i++) {
                String fullPath = path.getPath() + "/" + fileNames[i];
                String fileName = fileNames[i];
                if (fileName.length() > 11)
                    fileName = fileName.substring(0, 11);
                long size = new File(fullPath).length();
                imageProperties.add(new ImageProperties(fullPath, fileName, size));
            }
        }
        return imageProperties;
    }
}
