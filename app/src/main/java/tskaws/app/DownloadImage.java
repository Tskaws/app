package tskaws.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView image;
    Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

    public DownloadImage(ImageView image){
        this.image = image;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        if (cache.containsKey(urldisplay)) {
            return cache.get(urldisplay);
        }

        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        cache.put(urldisplay, mIcon11);

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}