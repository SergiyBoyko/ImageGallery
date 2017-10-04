package com.example.android.imagegallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbrsw on 03.10.2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "galleryPhotos";
    private static final String TABLE_PHOTOS = "photos";
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String PHOTOS_PATH = "CREATE TABLE " + TABLE_PHOTOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY autoincrement," + KEY_PATH + " TEXT" + ")";
        sqLiteDatabase.execSQL(PHOTOS_PATH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public String getTablePhotosName() {
        return TABLE_PHOTOS;
    }

    public String getKeyId() {
        return KEY_ID;
    }

    public String getKeyPath() {
        return KEY_PATH;
    }
}
