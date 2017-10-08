package com.instagram.cyril.cyrilsinstagramdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by cyril on 05/10/2017.
 */

public class UserSession {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final String MY_PREF = "User_Shared_Preference";
    private static final String USERNAME = "User_Name";
    private static final String USER_ID = "User_ID";
    private static final String FULL_NAME = "Full_Name";
    private static final String PROFILE_PIC_URL = "Profile_Pic";
    private static final String USER_BIO = "User_Bio";
    private static final String ACCESS_TOKEN = "User_Access_Token";
    private static final String USABLE_WIDTH = "Screen_Width";

    public UserSession(Context context) {
        sharedPref = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void storeAccessToken(String accessToken, String id, String username, String name, String profilePic, String bio) {
        Log.e("SESSION", "In Store AccessToken     bio is     " + bio);
        editor.putString(USER_ID, id);
        editor.putString(FULL_NAME, name);
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.putString(USERNAME, username);
        editor.putString(PROFILE_PIC_URL, profilePic);
        editor.putString(USER_BIO, bio);
        editor.commit();
    }

    public void storeAccessToken(String accessToken) {
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public void storeUsableWidth(String width) {
        editor.putString(USABLE_WIDTH, width);
        editor.commit();
    }

    public void resetAccessToken() {
        editor.putString(USER_ID, null);
        editor.putString(FULL_NAME, null);
        editor.putString(ACCESS_TOKEN, null);
        editor.putString(USERNAME, null);
        editor.putString(PROFILE_PIC_URL, null);
        editor.putString(USER_BIO, null);
        editor.commit();
    }

    public String getUsername() {
        return sharedPref.getString(USERNAME, null);
    }

    public String getProfilePicUrl() {
        return sharedPref.getString(PROFILE_PIC_URL, null);
    }

    public String getUserBio() {
        return sharedPref.getString(USER_BIO, null);
    }

    public String getId() {
        return sharedPref.getString(USER_ID, null);
    }

    public String getFullName() {
        return sharedPref.getString(FULL_NAME, null);
    }

    public String getAccessToken() {
        return sharedPref.getString(ACCESS_TOKEN, null);
    }

    public String getUsableWidth() {
        return sharedPref.getString(USABLE_WIDTH, null);
    }

}
