package com.robotemplates.cookbook.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.robotemplates.cookbook.interfaces.Constant;

public class Preference implements Constant {
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences.Editor editor;

    public static void setLogInPreference(Context ctx, String tag, String id, String name, String email, String profile_pic) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(KEY_TAG, tag);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PIC, profile_pic);
        editor.commit();
    }

    public static String getLoginId(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String id = sharedpreferences.getString(KEY_ID, null);
        return id;
    }

    public static String getName(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString(KEY_NAME, null);
        return name;
    }

    public static String getProfilePic(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String pic = sharedpreferences.getString(KEY_PROFILE_PIC, null);
        return pic;
    }

    public static String getTag(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String tag = sharedpreferences.getString(KEY_TAG, null);
        return tag;
    }

    public static void setCategoryId(Context ctx, String catId) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(KEY_CATEGORY_ID, catId);
        editor.commit();
    }

    public static String getCategoryId(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String catId = sharedpreferences.getString(KEY_CATEGORY_ID, null);
        return catId;
    }

    public static void setSearchQuery(Context ctx, String mSearchQuery) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(KEY_SEARCH_QUERY, mSearchQuery);
        editor.commit();
    }

    public static String getSearchQuery(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String mSearchQuery = sharedpreferences.getString(KEY_SEARCH_QUERY, null);
        return mSearchQuery;
    }
}
