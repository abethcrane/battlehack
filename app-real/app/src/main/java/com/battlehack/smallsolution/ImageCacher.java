package com.battlehack.smallsolution;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageCacher {
    private static Map<String, Bitmap> store = new HashMap<String, Bitmap>();


    public static void getImageAsync(ImageView iv, String url) {
        iv.setTag(url);
        new DownloadImagesTask().execute(iv);
    }

    public static void clearCache() {
        store.clear();
    }

    private static class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            String url = (String) imageView.getTag();
            if (!store.containsKey(url)) {
                store.put(url, download_Image(url));
            }
            return store.get(imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }


        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                is.close();
            } catch (Exception e) {
                Log.e("Err", "Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }
    }


}
