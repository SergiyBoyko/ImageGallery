package com.example.android.imagegallery;

import android.graphics.Bitmap;

/**
 * Created by fbrsw on 02.10.2017.
 */

public class ImageProperties {
    private String path;
    private String title;
    private long size;

    public ImageProperties(String path, String title, long size) {
        this.path = path;
        this.title = title;
        this.size = size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public long getSize() {
        return size;
    }
}
