package com.example.android.imagegallery;

import android.graphics.Bitmap;

/**
 * Created by fbrsw on 02.10.2017.
 */

public class ImageItem {
    private Bitmap image;
    private String title;
    private long size;

    public ImageItem(Bitmap image, String title, long size) {
        super();
        this.image = image;
        this.title = title;
        this.size = size;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
