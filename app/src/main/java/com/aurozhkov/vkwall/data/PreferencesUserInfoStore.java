package com.aurozhkov.vkwall.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUserInfoStore implements UserInfoStore {

    private static final String USER_KEY = "user";
    private static final String TOKEN_KEY = "token";

    private final SharedPreferences sharedPreferences;

    public static PreferencesUserInfoStore get(Context context) {
        return new PreferencesUserInfoStore(context, PreferencesUserInfoStore.class.getSimpleName());
    }

    private PreferencesUserInfoStore(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    @Override
    public void saveUserId(long id) {
        sharedPreferences.edit().putLong(USER_KEY, id).apply();
    }

    @Override
    public long getUserId() {
        return sharedPreferences.getLong(USER_KEY, -1);
    }

    @Override
    public void saveAccessToken(String accessToken) {
        sharedPreferences.edit().putString(TOKEN_KEY, accessToken).apply();
    }

    @Override
    public String getAccessToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    @Override
    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    @Override
    public boolean hasInfo() {
        return getAccessToken() != null;
    }
}
