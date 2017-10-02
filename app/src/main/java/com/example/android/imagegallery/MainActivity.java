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

    private ArrayList<ImageItem> loadData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        File path = new File(Environment.getExternalStorageDirectory(), "ImageGallery");
        if (path.exists()) {
            String[] fileNames = path.list();
            for (int i = 0; i < fileNames.length; i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(path.getPath() + "/" + fileNames[i]);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthBmp = imageInByte.length;
                String fileName = fileNames[i];
                if (fileName.length() > 11)
                    fileName = fileName.substring(0, 11);
//                if (lengthBmp < 500 * 1024)
                int coef = getCoefficient(bitmap);
                imageItems.add(new ImageItem(Bitmap.createScaledBitmap(
                        bitmap,
                        bitmap.getWidth() / coef,
                        bitmap.getHeight() / coef, false), fileName, lengthBmp));
                ///Now set this bitmap on imageview
            }
        }
        return imageItems;
    }

    private int getCoefficient(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int largeEdge = Math.max(width, height);
        int coef = 1;
        while (largeEdge / coef > 200)
            coef++;
        return coef;
    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            long lengthBmp = imageInByte.length;

            imageItems.add(new ImageItem(bitmap, "Image " + i, lengthBmp));
        }
        return imageItems;
    }
}
