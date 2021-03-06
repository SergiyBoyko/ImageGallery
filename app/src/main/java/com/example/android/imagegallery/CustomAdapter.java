package com.example.android.imagegallery;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by fbrsw on 02.10.2017.
 */

public class CustomAdapter extends BaseAdapter {
    private Context context; // MainActivity object (this)
    private ArrayList<ImageProperties> data = new ArrayList<ImageProperties>(); // imageItems
    private int layoutResourceId; // R.layout.grid_item_layout

    public void addItem(ImageProperties ip) {
        if (ip != null) {
            data.add(ip);
            notifyDataSetChanged();
        }
    }

    public void clean() {
        for (ImageProperties ip : data) {
            File file = new File(ip.getPath());
            boolean suc = file.delete();
        }
        data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean removeOne(ImageProperties ip) {
        boolean suc = false;
        if (data.contains(ip)) {
            File file = new File(ip.getPath());
            data.remove(ip);
            suc = file.delete();
        }
        notifyDataSetChanged();
        return suc;
    }

    // new CustomAdapter(this, R.layout.grid_item_layout, imageItems);
    public CustomAdapter(Context context, int layoutResourceId, ArrayList<ImageProperties> data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) { // int position, View convertView, ViewGroup parent
        View row = view;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, viewGroup, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageProperties imageProperties = data.get(i);
        holder.imageTitle.setText(imageProperties.getTitle());

        Uri uri = Uri.fromFile(new File(imageProperties.getPath()));

        Picasso // glide
                .with(context)
                .load(uri)
                .resize(96, 96)
                .centerCrop()
                .into(holder.image);

        return row;
    }

    private static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}
