package com.instagram.cyril.cyrilsinstagramdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class ProfileMultipleViewActivity extends AppCompatActivity {
    private UserSession userSession;
    private RelativeLayout popUpButton;
    private String SELF_URL = "https://api.instagram.com/v1/users/self/?access_token=";
    private String RECENT_URL = "https://api.instagram.com/v1/users/self/media/recent/?access_token=";
    private ListView recentPostsView;
    private ListAdapter adapter;
    private BaseAdapter gridAdapter;
    private ImageView profilePic, linearViewButton, gridViewButton, changeViewIcon;
    private RelativeLayout changeViewButton, refreshContentButton, logUserOutButton, scrollViewUpButton;
    private TextView posts, followers, followedBy, profileName, bio, buttonText;
    private String userPosts, userFolowers, userFollowedBy;
    private ArrayList userPostlist = new ArrayList();
    private ArrayList existingUserPostList = new ArrayList();
    private GridView recentPostsGridView;
    private ListViewFormatter formatter;
    private int dpToPx;
    private android.app.AlertDialog alertDialog;
    private ScrollView profile;
    private LinearLayout scrollViewLayout;
    private Boolean isLinearShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            alertDialog = new android.app.AlertDialog.Builder(ProfileMultipleViewActivity.this).show();
            alertDialog.dismiss();
            setContentView(R.layout.activity_profile_multiple_view);
            userSession = new UserSession(ProfileMultipleViewActivity.this);
            Display display = getWindowManager().getDefaultDisplay();
            userSession.storeUsableWidth(Integer.toString(display.getWidth()));
            formatter = new ListViewFormatter(Math.round(getResources().getSystem().getDisplayMetrics().density), display.getWidth());
            findViews();
            activateProfile();

            popUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    popMenuUp();
                }
            });
            gridViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popMenuDown();
                    showGridView();
                }
            });
            scrollViewLayout.setOnTouchListener(viewTouchListener);
            recentPostsView.setOnTouchListener(viewTouchListener);
            recentPostsGridView.setOnTouchListener(viewTouchListener);
            linearViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popMenuDown();
                    showLinearView();
                }
            });
            logUserOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        popMenuDown();
                        final Dialog dialog = new Dialog(ProfileMultipleViewActivity.this, R.style.TransparentTheme);
                        dialog.setContentView(R.layout.logout_pop_up);
                        dialog.setCanceledOnTouchOutside(false);
                        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);
                        TextView logOut = (TextView) dialog.findViewById(R.id.logoutButton);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        logOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                logUserOut();
                            }
                        });
                        dialog.show();
                    } catch (Exception e) {
                        Log.e("EXC", "Exception caught from loguserOutButton_oncicklistener  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
                    }
                }
            });
            scrollViewUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        popMenuDown();
                        profile.smoothScrollTo(0, 0);
                    } catch (Exception e) {
                        Log.e("EXC", "Exception caught from scrollViewButton_onclick  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
                    }
                }
            });
            refreshContentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        popMenuDown();
                        new ReadUserDetails().execute();
                    } catch (Exception e) {
                        Log.e("EXC", "Exception caught from reshcontentbutton_click  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
                    }
                }
            });
            changeViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        popMenuDown();
                        if (isLinearShow)
                            showGridView();
                        else showLinearView();
                    } catch (Exception e) {
                        Log.e("EXC", "Exception caught from changeViewButton_click  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from onCreate  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    private void showLinearView() {
        try {
            isLinearShow = true;
            changeViewIcon.setImageResource(R.mipmap.grid_white);
            linearViewButton.setImageResource(R.mipmap.selected_linear_symbol);
            gridViewButton.setImageResource(R.mipmap.unselected_grid_symbol);
            if (userPostlist.size() > 0) {
                if (adapter != null)
                    if (adapter.getCount() > 0) {

                        recentPostsGridView.setVisibility(View.GONE);
                        recentPostsView.setVisibility(View.VISIBLE);

                        //recentPostsView.setAlpha(1f);
                        //recentPostsGridView.setAlpha(0f);
                    } else {
                        recentPostsGridView.setVisibility(View.GONE);
                        adapter = new CustomAdapter(ProfileMultipleViewActivity.this, userPostlist);
                        recentPostsView.setAdapter(adapter);
                        recentPostsView.setVisibility(View.VISIBLE);
                    }
            }
            profile.smoothScrollTo(0, 0);
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from showLinearView  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    private void showGridView() {
        try {
            isLinearShow = false;
            changeViewIcon.setImageResource(R.mipmap.linear_white);
            gridViewButton.setImageResource(R.mipmap.selected_grid_symbol);
            linearViewButton.setImageResource(R.mipmap.unselected_linear_symbol);
            if (userPostlist.size() > 0) {
                if (gridAdapter != null)
                    if (gridAdapter.getCount() > 0) {
                        recentPostsView.setVisibility(View.GONE);
                        //if(recentPostsGridView.getVisibility()==View.GONE)
                        recentPostsGridView.setVisibility(View.VISIBLE);
                        //recentPostsView.setAlpha(0f);
                        //recentPostsGridView.setAlpha(1f);
                    } else {
                        recentPostsView.setVisibility(View.GONE);
                        gridAdapter = new CustomGridAdapter(ProfileMultipleViewActivity.this, userPostlist);
                        recentPostsGridView.setAdapter(gridAdapter);
                        formatter.setGridViewSize(recentPostsGridView, userSession.getUsableWidth());
                        recentPostsGridView.setVisibility(View.VISIBLE);
                    }
            }
            profile.smoothScrollTo(0, 0);
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from showGridView  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    private void popMenuUp() {
        try {
            if (scrollViewUpButton.getVisibility() == View.VISIBLE) {
                buttonText.setText("+");
                scrollViewUpButton.setVisibility(View.GONE);
                logUserOutButton.setVisibility(View.GONE);
                changeViewButton.setVisibility(View.GONE);
                refreshContentButton.setVisibility(View.GONE);
            } else {
                buttonText.setText("x");
                scrollViewUpButton.setVisibility(View.VISIBLE);
                logUserOutButton.setVisibility(View.VISIBLE);
                changeViewButton.setVisibility(View.VISIBLE);
                refreshContentButton.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from popMenuUp  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    public void popMenuDown() {
        try {
            if (scrollViewUpButton.getVisibility() == View.VISIBLE) {
                buttonText.setText("+");
                scrollViewUpButton.setVisibility(View.GONE);
                logUserOutButton.setVisibility(View.GONE);
                changeViewButton.setVisibility(View.GONE);
                refreshContentButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from popmenuDown  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    private void logUserOut() {
        try {
            userSession.resetAccessToken();
            finish();
            startActivity(new Intent(ProfileMultipleViewActivity.this, MainActivity.class));
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from logUserOut  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    private void findViews() {
        try {
            changeViewIcon = (ImageView) findViewById(R.id.gridButtonIcon);
            scrollViewLayout = (LinearLayout) findViewById(R.id.outerScrollLayout);
            logUserOutButton = (RelativeLayout) findViewById(R.id.logButton);
            scrollViewUpButton = (RelativeLayout) findViewById(R.id.scrollButton);
            refreshContentButton = (RelativeLayout) findViewById(R.id.refreshButton);
            changeViewButton = (RelativeLayout) findViewById(R.id.gridButton);
            buttonText = (TextView) findViewById(R.id.popUpButtonText);
            popUpButton = (RelativeLayout) findViewById(R.id.logoutButton);
            recentPostsView = (ListView) findViewById(R.id.recentPostsListView);
            profilePic = (ImageView) findViewById(R.id.profilePic);
            posts = (TextView) findViewById(R.id.postsTextField);
            followers = (TextView) findViewById(R.id.followersTextField);
            followedBy = (TextView) findViewById(R.id.followingTextField);
            profileName = (TextView) findViewById(R.id.profileName);
            bio = (TextView) findViewById(R.id.bioTextField);
            linearViewButton = (ImageView) findViewById(R.id.linearViewButton);
            gridViewButton = (ImageView) findViewById(R.id.gridViewButton);
            recentPostsGridView = (GridView) findViewById(R.id.recentPostsGridView);
            profile = (ScrollView) findViewById(R.id.profileScrollView);
        } catch (Exception exception) {
            Log.e("EXC", "Exception caught from findVies    Profile Activity" + exception.getLocalizedMessage());
        }
    }

    private void activateProfile() {
        try {

            profileName.setText(userSession.getFullName());
            bio.setText(userSession.getUserBio());
            new ReadUserDetails().execute();
            new DownloadImageTask(ProfileMultipleViewActivity.this, profilePic)
                    .execute(userSession.getProfilePicUrl());
        } catch (Exception exception) {
            Log.e("EXC", "Exception caught from activateProfile    Profile Activity       " + exception.getLocalizedMessage());
        }
    }

    private class ReadUserDetails extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                if (userSession.getAccessToken() != null) {
                    //SELF_URL += userSession.getAccessToken();
                    HttpHandler sh = new HttpHandler();
                    Log.e("SELF", "Going to invoke self url     " + SELF_URL);
                    String jsonStr = sh.makeServiceCall(SELF_URL + userSession.getAccessToken());

                    Log.e("JSON_RESPONSE", "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = (JSONObject) new JSONTokener(jsonStr).nextValue();
                            userPosts = jsonObj.getJSONObject("data").getJSONObject("counts").getString("media");
                            userFolowers = jsonObj.getJSONObject("data").getJSONObject("counts").getString(
                                    "followed_by");
                            userFollowedBy = jsonObj.getJSONObject("data").getJSONObject("counts").getString(
                                    "follows");
                        } catch (final JSONException e) {
                            Log.e("ERROR", "Json parsing error: " + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        return true;

                    } else {
                        Log.e("JSON_ERROR", "Couldn't get json from server.  ReadUserDetails   ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //displayAlert("Network Delay!", "Please check your connection and try again.");
                                Toast.makeText(getApplicationContext(),
                                        "Server not reachable!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                        return false;
                    }
                } else {
                    Toast.makeText(ProfileMultipleViewActivity.this, "Failed to retrieve access token", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } catch (Exception e) {
                Log.e("EXC", "Exception caught from asynchronous doInBackground method   " + e.getLocalizedMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                super.onPostExecute(result);
                if (result) {
                    posts.setText(userPosts);
                    followers.setText(userFolowers);
                    followedBy.setText(userFollowedBy);
                }
                new ReadRecentPosts().execute();
            } catch (Exception e) {
                Log.e("EXC", "Exception caught from asynchronous onPostExecute method   " + e.getLocalizedMessage());
            }
        }
    }

    private class ReadRecentPosts extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                String readPostsURL = RECENT_URL + userSession.getAccessToken();
                if (userSession.getAccessToken() != null) {
                    HttpHandler sh = new HttpHandler();
                    Log.e("SELF", "Going to invoke self url     " + RECENT_URL + userSession.getAccessToken());
                    String jsonStr = sh.makeServiceCall(readPostsURL);

                    Log.e("JSON_RESPONSE", "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = (JSONObject) new JSONTokener(jsonStr).nextValue();
                            if (jsonObj.getJSONObject("meta").getString("code").equals("200")) {
                                JSONArray dataArray = (JSONArray) jsonObj.getJSONArray("data");
                                if (existingUserPostList.size() != 0) {
                                    existingUserPostList = new ArrayList();
                                    existingUserPostList = userPostlist;
                                }
                                userPostlist = new ArrayList<>();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    PostData postData = new PostData(dataArray.getJSONObject(i).getString("id"), dataArray.getJSONObject(i).getJSONObject("user").getString("id"), dataArray.getJSONObject(i).getJSONObject("user").getString("full_name"), dataArray.getJSONObject(i).getJSONObject("user").getString("profile_picture"), dataArray.getJSONObject(i).getJSONObject("user").getString("username"), dataArray.getJSONObject(i).getString("created_time"), dataArray.getJSONObject(i).getJSONObject("caption").getString("text"), dataArray.getJSONObject(i).getBoolean("user_has_liked"), dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url"), dataArray.getJSONObject(i).getJSONObject("likes").getInt("count"), dataArray.getJSONObject(i).getJSONObject("comments").getInt("count"), dataArray.getJSONObject(i).getString("location"), dataArray.getJSONObject(i).getString("type"), dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail").getString("url"));
                                    postData.setImageDetails(dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getInt("width"), dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
                                    if (dataArray.getJSONObject(i).getString("type").equals("video")) {
                                        postData.setVideoURL(dataArray.getJSONObject(i).getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));
                                        postData.setImageDetails(dataArray.getJSONObject(i).getJSONObject("videos").getJSONObject("standard_resolution").getInt("width"), dataArray.getJSONObject(i).getJSONObject("videos").getJSONObject("standard_resolution").getInt("height"));
                                    }
                                    ArrayList<String> tags = new ArrayList<>();
                                    JSONArray tagArray = dataArray.getJSONObject(i).getJSONArray("tags");
                                    for (int j = 0; j < tagArray.length(); j++) {
                                        tags.add(tagArray.getString(j));
                                    }
                                    postData.setTags(tags);

                                    ArrayList<String> mediaFiles = new ArrayList<>();
                                    if (dataArray.getJSONObject(i).has("carousel_media")) {
                                        JSONArray subMedias = dataArray.getJSONObject(i).getJSONArray("carousel_media");
                                        for (int j = 0; j < subMedias.length(); j++) {
                                            mediaFiles.add(subMedias.getJSONObject(j).getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                                        }
                                    }
                                    postData.setCarousel_mediaUrlList(mediaFiles);
                                    userPostlist.add(postData);
                                }
                            } else
                                Toast.makeText(ProfileMultipleViewActivity.this, "Recent Posts request failed", Toast.LENGTH_SHORT).show();

                        } catch (final JSONException e) {
                            Log.e("ERROR", "Json parsing error: " + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                    } else {
                        Log.e("JSON_ERROR", "Couldn't get json from server.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayAlert("Network Delay!", "Please check your connection and try again.");
                                Toast.makeText(getApplicationContext(),
                                        "Server not reachable!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    }
                } else
                    Toast.makeText(ProfileMultipleViewActivity.this, "Failed to load accesstoken", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("EXC", "Exception caught from doInBackground method while accessing recent posts  " + e.getLocalizedMessage());

                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try {
                if (!result) {
                    if (userPostlist.size() == 0) {
                        TextView statusText = (TextView) findViewById(R.id.statusText);
                        statusText.setText("Failed to read from the server !");
                        statusText.setVisibility(View.VISIBLE);
                        Log.e("UPDATE", " result is false and post size is zero         ********");
                    } else
                        Log.e("UPDATE", " result is false and post size is not zero         ********");
                } else if (userPostlist.size() > 0) {
                    Log.e("UPDATE", " result is true and post size greater than zero         ********");
                    if (existingUserPostList.size() != (userPostlist.size())) {
                        adapter = new CustomAdapter(ProfileMultipleViewActivity.this, userPostlist);
                        recentPostsView.setAdapter(adapter);
                        LinearLayout protileTopBar = (LinearLayout) findViewById(R.id.profileTopTab);
                        protileTopBar.measure(0, 0);
                        Log.e("UPDATE", "updated        ***********" + existingUserPostList.size() + "   -  " + userPostlist.size());
                        Log.e("first", " First    ***** " + existingUserPostList.toString());
                        Log.e("second", "Second **********  " + userPostlist.toString());
                        existingUserPostList = new ArrayList();
                        existingUserPostList = userPostlist;
                        //recentPostsView.setOnTouchListener(listViewTouchListener);
                        //recentPostsView.setClickable(true);
                        //recentPostsView.setNestedScrollingEnabled(true);
                        formatter.setListViewSize(recentPostsView, protileTopBar.getMeasuredHeight());

                        gridAdapter = new CustomGridAdapter(ProfileMultipleViewActivity.this, userPostlist);
                        recentPostsGridView.setVisibility(View.VISIBLE);
                        recentPostsGridView.setAdapter(gridAdapter);
                        formatter.setGridViewSize(recentPostsGridView, userSession.getUsableWidth());
                        recentPostsGridView.setVisibility(View.GONE);
                    } else
                        Log.e("UPDATE", "not updated since same    ****");
                } else {
                    Log.e("UPDATE", " result is true and post size not greater than zero         ********");
                    TextView statusText = (TextView) findViewById(R.id.statusText);
                    statusText.setText("No posts yet !");
                    statusText.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("EXC", "Exception caught from postExecute_readrecentpostthread  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
            }
        }

    }

    ListView.OnTouchListener listViewTouchListener = new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    };

    private void displayAlert(String heading, String message) {
        try {
            if (alertDialog.isShowing())
                alertDialog.dismiss();
            TextView title = new TextView(ProfileMultipleViewActivity.this);
            title.setText(heading);
            title.setTextColor(Color.BLACK);
            title.setTextSize(22);
            title.setGravity(Gravity.CENTER);
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileMultipleViewActivity.this);
            builder.setCustomTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whjich) {
                }
            });
            alertDialog = builder.show();
            TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
        } catch (Exception e) {
            Log.e("EXC", "Exception caught from displayAlert  ProfileMultipleViewActivity  " + e.getLocalizedMessage());
        }
    }

    View.OnTouchListener viewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            popMenuDown();
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        new GracefullApplicationClosure(ProfileMultipleViewActivity.this);
    }
}
