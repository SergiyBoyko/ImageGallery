package com.example.android.imagegallery;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by fbrsw on 02.10.2017.
 */

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        String title = getIntent().getStringExtra("title");
        String path = getIntent().getStringExtra("image");
        long length = getIntent().getLongExtra("length", 1);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.image);

        Uri uri = Uri.fromFile(new File(path));

        Picasso // glide
                .with(this)
                .load(uri)
                .into(imageView);

        String len = String.valueOf(length / (1024)) + "KB";

        TextView lenTextView = (TextView) findViewById(R.id.img_len);
        lenTextView.setText(len);
    }
}
