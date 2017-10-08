package com.instagram.cyril.cyrilsinstagramdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private RelativeLayout instagramButton;
    private TextView titleText;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private CookieManager cookieManager;
    private String accessToken;
    private UserSession sessionObject;
    private android.app.AlertDialog alertDialog;

    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/?client_id="
            + ApplicationData.CLIENT_ID + "&redirect_uri=" + ApplicationData.REDIRECT_URI
            + "&response_type=code&display=touch&scope=likes+comments+relationships";
    private static final String ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token?client_id="
            + ApplicationData.CLIENT_ID + "&client_secret="
            + ApplicationData.CLIENT_SECRET + "&redirect_uri=" + ApplicationData.REDIRECT_URI;
    private static final String API_URL = "https://api.instagram.com/v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            alertDialog=new android.app.AlertDialog.Builder(MainActivity.this).show();
            alertDialog.dismiss();
            sessionObject = new UserSession(MainActivity.this);
            if (sessionObject.getAccessToken() != null) {
                finish();
                startActivity(new Intent(MainActivity.this, ProfileMultipleViewActivity.class));
            } else {
                setContentView(R.layout.activity_main);
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                findViews();
                CookieSyncManager.createInstance(getApplicationContext());
                cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();

                instagramButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enableWebView();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("Exception", "Exception caught from onCreate  *****     MainActivity");
        }
    }

    private void findViews() {
        instagramButton = (RelativeLayout) findViewById(R.id.instagramButton);
        titleText = (TextView) findViewById(R.id.title);
    }

    private void enableWebView() {
        try {
            dialog = new Dialog(MainActivity.this, R.style.AppTheme);
            dialog.setContentView(R.layout.instagram_login_popup);
            dialog.setCanceledOnTouchOutside(false);
            //dialog.setCancelable(false);
            dialog.show();
            WebView webView = (WebView) dialog.findViewById(R.id.instagramWebView);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new InstagramWebViewClient());
            webView.loadUrl(AUTH_URL);
        } catch (Exception e) {
            Log.e("Exception", "Exception caught from enableWebView  *****     MainActivity");
        }
    }



    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                Log.d("WEBVIEW", "Redirecting URL " + url);

                if (url.startsWith(ApplicationData.REDIRECT_URI)) {
                    String urls[] = url.split("=");
                    getAccessToken(urls[1]);
                    Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    instagramButton.setVisibility(View.GONE);
                    return true;
                }
                return false;
            } catch (Exception e) {
                Log.e("Exception", "Exception caught from shouldOverRideUrlLoading  *****     WebViewClient");
                return false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d("WEBVIEW", "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            dialog.dismiss();
            if (description.contains("net"))
                displayAlert("Error!", "Please check your network connection and try again!\n Error description  :- "+description);
            else
                displayAlert("Error!", description);
            //mListener.onError(description);
            //dialog.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WEBVIEW", "Loading URL: " + url);

            super.onPageStarted(view, url, favicon);
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                WebView webView = (WebView) dialog.findViewById(R.id.instagramWebView);
                String title = webView.getTitle();
                Log.e("|TITL|E","From page finished.. title is   "+title);
                if (title != null && title.length() > 0) {
                    if (title.length() > 25 && !title.contains("Authorization Request — Instagram")) {
                        displayAlert("Error!", "Oops! Something went wrong. Please try again.");
                    } else
                        titleText.setText(title);
                }

                Log.d("WEBVIEW", "onPageFinished URL: " + url);
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("EXC", "Exception caught form Main activity on page finished     " + e.getLocalizedMessage());
                displayAlert("Error!", "Oops! Something went wrong. Please try again.");
            }
        }

    }


    private void displayAlert(String heading, String message) {
        if(alertDialog.isShowing())
            alertDialog.dismiss();
        TextView title = new TextView(MainActivity.this);
        title.setText(heading);
        title.setTextColor(Color.BLACK);
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int whjich) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    cookieManager.removeAllCookie();
                }
                titleText.setText("Instagram");
            }
        });
        alertDialog = builder.show();
        TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
    }

    private void getAccessToken(final String code) {

        new Thread() {
            @Override
            public void run() {
                Log.e("ACCESS", "Getting access token");
                try {
                    URL url = new URL(ACCESS_TOKEN_URL);
                    Log.e("ACCESS", "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());
                    writer.write("client_id=" + ApplicationData.CLIENT_ID + "&client_secret="
                            + ApplicationData.CLIENT_SECRET + "&grant_type=authorization_code"
                            + "&redirect_uri=" + ApplicationData.REDIRECT_URI + "&code=" + code);
                    writer.flush();
                    String response = convertStreamToString(urlConnection
                            .getInputStream());
                    Log.i("ACCESS_RESPONSE", "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();

                    accessToken = jsonObj.getString("access_token");
                    Log.i("ACCESS TOKEN", "Got access token: " + accessToken);

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString(
                            "username");
                    String fullName = jsonObj.getJSONObject("user").getString(
                            "full_name");
                    String profilePic = jsonObj.getJSONObject("user").getString(
                            "profile_picture");
                    String bio = jsonObj.getJSONObject("user").getString(
                            "bio");
                    sessionObject.storeAccessToken(accessToken, id, user, fullName, profilePic, bio);
                    dialog.dismiss();
                    finish();
                    startActivity(new Intent(MainActivity.this, ProfileMultipleViewActivity.class));

                } catch (Exception e) {
                    Log.e("Exc", "Exception caught from accessSessionThread    main activity   " + e.getLocalizedMessage());
                }
            }
        }.start();
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        String string = "";

        if (inputStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                reader.close();
            } finally {
                inputStream.close();
            }

            string = stringBuilder.toString();
        }

        return string;
    }

}
