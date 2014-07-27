package com.battlehack.smallsolution;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;


public class Vendor implements Serializable{

    public String id, name, price, org, item, url;

    public Vendor(String id, String name, String price, String org, String item, String url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.org = org;
        this.item = item;
        this.url = url;
    }

    public static Vendor fromJSON(JSONObject obj) throws JSONException {

        String id = obj.getString("id");
        String name = obj.getString("vendor");
        String price = obj.getString("price");
        String org = obj.getString("organisation");
        String item = obj.getString("item_name");
        String url = obj.getString("url");
        return new Vendor(id, name, price, org, item, url);
    }




}
