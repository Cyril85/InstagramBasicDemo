package com.instagram.cyril.cyrilsinstagramdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by cyril on 05/10/2017.
 */

public class ImageFormatter {

    private Bitmap bmp;

    public ImageFormatter(Bitmap bitmap) {
        this.bmp = bitmap;
    }


    public Bitmap getRoundedBitmap(int radius) {
        try {
            Bitmap sbmp;
            final int maxSize = radius;
            int outWidth;
            int outHeight;
            int inWidth = bmp.getWidth();
            int inHeight = bmp.getHeight();
            if (inWidth > inHeight) {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            } else {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            }
            sbmp = Bitmap.createScaledBitmap(bmp, outWidth, outHeight, false);
            Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final String color = "#BAB399";
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, radius, radius);

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor(color));
            canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                    radius / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            if (sbmp.getHeight() > sbmp.getWidth())
                canvas.drawBitmap(sbmp, 0, -Math.round((sbmp.getHeight() - radius) / 2), paint);
            else
                canvas.drawBitmap(sbmp, -Math.round((sbmp.getWidth() - radius) / 2), 0, paint);

            return output;
        } catch (Exception e) {
            Log.e("EXC", "Exception caught form getRoundedBitmap method from   imageDownloader   *****************  " + e.getLocalizedMessage());
            return null;
        }
    }
}
