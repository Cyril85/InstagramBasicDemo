package com.instagram.cyril.cyrilsinstagramdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by cyril on 06/10/2017.
 */

public class CustomPageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> urlList;
    private LayoutInflater layoutInflater;
    private Drawable bmp;

    public CustomPageAdapter(Context context, ArrayList<String> urlList, Drawable bmp) {
        this.context = context;
        this.urlList = urlList;
        this.bmp = bmp;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view.equals((LinearLayout) object));
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View customView = null;
        try {
            layoutInflater = LayoutInflater.from(context);
            customView = layoutInflater.inflate(R.layout.swipe_image_view, container, false);
            ImageView postedImage = (ImageView) customView.findViewById(R.id.postImage);
            TextView imagePosition = (TextView) customView.findViewById(R.id.imagePosition);
            if (bmp != null) {
                postedImage.setImageDrawable(bmp);
                /*
                int height = postedImage.getLayoutParams().height;
                int width = postedImage.getLayoutParams().width;
                UserSession session = new UserSession(context);
                height = Math.round(Integer.parseInt(session.getUsableWidth()) * height / width);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) postedImage.getLayoutParams();
                params.height=height;
                params.topMargin=0;
                params.width=Integer.parseInt(session.getUsableWidth());
                postedImage.setLayoutParams(params);
                */
                  }
            new DownloadImageTask(false, postedImage)
                    .execute(urlList.get(position));
            imagePosition.setText(position + 1 + "/" + urlList.size());
            container.addView(customView);
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from instatiateitem  CustompagerAdapter     " + e.getLocalizedMessage());
        }
        return customView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


    private class DownloadImageTask extends AsyncTask<String, Boolean, Bitmap> {
        ImageView bmImage;
        Boolean isroundedImage = false;

        public DownloadImageTask(Boolean isroundedImage, ImageView bmImage) {
            this.bmImage = bmImage;
            this.isroundedImage = isroundedImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getLocalizedMessage());
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (isroundedImage) {
                ImageFormatter formatter = new ImageFormatter(result);
                bmImage.setImageBitmap(formatter.getRoundedBitmap(200));
            } else bmImage.setImageBitmap(result);
        }
    }
}
