package com.example.learnerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.learnerapp.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String USER_KEY = "user";

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(USER_KEY, userJson);
        editor.apply();
    }

    public User getUser() {
        String userJson = prefs.getString(USER_KEY, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearUser() {
        editor.clear();
        editor.apply();
    }
}

