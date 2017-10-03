package com.example.android.imagegallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    private GridView gridView;
    private CustomAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ImageProperties> imageProp = loadImageProperties();

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new CustomAdapter(this, R.layout.grid_item_layout, imageProp);
        gridView.setAdapter(gridAdapter);

        setListener();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    File myFile = new File(selectedImage.getPath());
                    if (myFile.exists()) {
                        String name = myFile.getName();
                        String absolute = myFile.getAbsolutePath();
                        long size = myFile.length();
                        gridAdapter.addItem(new ImageProperties(absolute, name, size));
                        Toast.makeText(this, "Photo added" + absolute, Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //String.valueOf(System.currentTimeMillis()) + ".jpg"
        String name = "Pic_" + gridAdapter.getCount() + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), "ImageGallery/" + name);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
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
