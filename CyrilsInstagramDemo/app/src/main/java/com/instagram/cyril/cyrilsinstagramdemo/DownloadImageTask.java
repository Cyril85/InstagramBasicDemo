package com.instagram.cyril.cyrilsinstagramdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by cyril on 06/10/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Context , DownloadImageTask.ImageAndUrl> {
    ImageView bmImage;
    Boolean isroundedImage=false;
     UserSession session;

    public DownloadImageTask(Context ctx,ImageView bmImage) {
        this.bmImage = bmImage;
        session=new UserSession(ctx);
    }

    protected ImageAndUrl doInBackground(String... urls) {
        try {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getLocalizedMessage());
            }
            ImageAndUrl data=new ImageAndUrl(mIcon11,urls[0]);
            return data;
        }catch (Exception e)
        {
            Log.e("EC","Exception caught from background method in DownloadImageTask              "+e.getLocalizedMessage());
            return  null;
        }
    }

    protected void onPostExecute(ImageAndUrl data) {
        if(data.url.equals(session.getProfilePicUrl())) {
            ImageFormatter formatter = new ImageFormatter(data.bitmap);
            bmImage.setImageBitmap(formatter.getRoundedBitmap(200));
        }else bmImage.setImageBitmap(data.bitmap);
    }

    public class ImageAndUrl
    {
        public Bitmap bitmap;
        public String url;

        public ImageAndUrl(Bitmap bitmap, String url) {
            this.bitmap = bitmap;
            this.url = url;
        }
    }
}