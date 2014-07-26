package com.battlehack.smallsolution;

import org.json.JSONException;
import org.json.JSONObject;

public class Vendor {

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
