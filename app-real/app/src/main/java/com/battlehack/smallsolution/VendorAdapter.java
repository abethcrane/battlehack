package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class VendorAdapter extends ArrayAdapter<Vendor> {

    Context context;
    int layoutResourceId;

    public VendorAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId, new ArrayList<Vendor>());
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VendorHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VendorHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtExtra = (TextView)row.findViewById(R.id.txtExtra);

            row.setTag(holder);
        } else {
            holder = (VendorHolder)row.getTag();
        }
        Vendor v = super.getItem(position);
        holder.txtTitle.setText(v.name);
        holder.txtExtra.setText(v.item + " - $" + v.price);
        v.getImageAsync(holder.imgIcon);
        return row;
    }

    static class VendorHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtExtra;
    }

}
