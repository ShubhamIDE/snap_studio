package com.photo.editor.snapstudio;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {
    private OnBitmapLoadedListener listener;

    public BitmapFromUrlTask(OnBitmapLoadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (listener != null) {
            listener.onBitmapLoaded(result);
        }
    }

    public interface OnBitmapLoadedListener {
        void onBitmapLoaded(Bitmap bitmap);
    }
}
