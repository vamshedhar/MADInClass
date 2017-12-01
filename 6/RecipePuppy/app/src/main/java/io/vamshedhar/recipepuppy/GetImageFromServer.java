package io.vamshedhar.recipepuppy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/2/17 7:01 PM.
 * vchinta1@uncc.edu
 */

public class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {

    IImage activity;

    public GetImageFromServer(IImage activity) {
        this.activity = activity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        RequestParams request = new RequestParams("GET", params[0]);

        try {
            HttpURLConnection con = request.setupConnection();
            Bitmap image = BitmapFactory.decodeStream(con.getInputStream());
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        activity.setImage(bitmap);
    }

    interface IImage{
        void setImage(Bitmap image);
    }
}
