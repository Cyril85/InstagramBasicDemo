package com.instagram.cyril.cyrilsinstagramdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by cyril on 28/09/2017.
 */

public class GracefullApplicationClosure {
    Context context;

    public GracefullApplicationClosure(Context context) {
        try {
            this.context = context;
            promptClosing();
        } catch (Exception e) {
            Log.e("Exxcc", "Exception caught from constructor GracefullApplicationClosure");
        }
    }

    public void promptClosing() {
        try {
            final AlertDialog alertDialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to exit the app?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whjich) {
                    try {
                   /* finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    */
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(homeIntent);
                        System.exit(0);
                    } catch (Exception e) {
                        Log.e("Exxcc", "Exception caught from positivebuttonclick_promptclosure GracefullApplicationClosure");
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog = builder.show();
            TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(context.getResources().getColor(R.color.colorLightBlue));
            nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbutton.setTextColor(context.getResources().getColor(R.color.colorLightBlue));
            messageText.setGravity(Gravity.CENTER);
            messageText.setTypeface(null, Typeface.BOLD);
            messageText.setTextSize(17);
        } catch (Exception e) {
            Log.e("EXX", "Exception caught from promptclosing method MainActivity   " + e.getLocalizedMessage());
        }
    }

}
