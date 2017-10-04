package com.example.android.imagegallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

    private DataBaseHandler dbHandler;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DataBaseHandler(this);
        db = dbHandler.getWritableDatabase();

        ArrayList<ImageProperties> imageProp = synWithDB();

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

                        ContentValues cv = new ContentValues();
                        db = dbHandler.getWritableDatabase();
                        cv.put(dbHandler.getKeyPath(), absolute);
                        long rowID = db.insert(dbHandler.getTablePhotosName(), null, cv);
                        //+ rowID + " " 
                        Toast.makeText(this, "Photo added " + absolute, Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String name = "Pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
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

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                final long finalID = id;
                final int finalPOS = pos;
                //Start details activity
                AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
                builder.setMessage("Are you sure you want to DELETE this photo?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                boolean b = deleteTitle(((ImageProperties) gridAdapter.getItem(finalPOS)));
                                Toast.makeText(MainActivity.this,
                                        "remove is " + (b ? "successful" : "failed"),
                                        Toast.LENGTH_LONG).show();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Toast.makeText(MainActivity.this, "canceled", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

        ((Button) findViewById(R.id.cleaner_button)).setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to DELETE database (Only for developers)?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                clean(view);
                                deleteDatabase(dbHandler.getDatabaseName());
                                dbHandler = new DataBaseHandler(MainActivity.this);
                                db = dbHandler.getWritableDatabase();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });
    }

    private ArrayList<ImageProperties> synWithDB() {
        final ArrayList<ImageProperties> imageProperties = new ArrayList<>();

        Cursor c = db.query(dbHandler.getTablePhotosName(), null, null, null, null, null, null);
        if (c.getCount() == 0) {
            Toast.makeText(this, "Empty DB", Toast.LENGTH_LONG).show();
        } else if (c.moveToFirst()) {
//            int idColIndex = c.getColumnIndex("id");
            int pathColIndex = c.getColumnIndex(dbHandler.getKeyPath());
            do {
                String path = c.getString(pathColIndex);
//                String res = "ID = " + c.getInt(idColIndex) +
//                        ", path = " + c.getString(pathColIndex);
                File file = new File(path);
                if (file.exists()) {
                    long size = file.length();
                    imageProperties.add(new ImageProperties(path, file.getName(), size));
                } else {
                    boolean suc = db.delete(dbHandler.getTablePhotosName(), dbHandler.getKeyPath() + "='" + path + "'", null) > 0;
                    Toast.makeText(this, path + " was deleted!", Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(this, res + "\n" + path, Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        }
        c.close();
//        dbHandler.close();
        return imageProperties;
    }

    public void clean(View view) {

        int clearCount = db.delete("photos", null, null);
//        db.execSQL("delete from "+ dbHandler.getTablePhotosName());
        Toast.makeText(this, "removed " + clearCount + " photo(s)", Toast.LENGTH_LONG).show();

        gridAdapter.clean();
//        dbHandler.close();
    }

    public boolean deleteTitle(ImageProperties ip) {
        boolean suc = db.delete(dbHandler.getTablePhotosName(), dbHandler.getKeyPath() + "='" + ip.getPath() + "'", null) > 0;
        if (suc) {
            boolean forToast = gridAdapter.removeOne(ip);
        }
        return suc;
    }
}
