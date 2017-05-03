package de.live.gdev.timetracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import de.live.gdev.timetracker.BuildConfig;
import de.live.gdev.timetracker.R;

public class AppSettings {
    private final SharedPreferences prefApp;
    private final Context context;

    public AppSettings(Context context) {
        this.context = context.getApplicationContext();
        prefApp = this.context.getSharedPreferences("app", Context.MODE_PRIVATE);
    }

    public Context getApplicationContext() {
        return context;
    }

    public void clearAppSettings() {
        prefApp.edit().clear().commit();
    }

    public String getKey(int stringKeyResourceId) {
        return context.getString(stringKeyResourceId);
    }

    public boolean isKeyEqual(String key, int stringKeyRessourceId) {
        return key.equals(getKey(stringKeyRessourceId));
    }

    private void setString(SharedPreferences pref, int keyRessourceId, String value) {
        pref.edit().putString(context.getString(keyRessourceId), value).apply();
    }

    private void setInt(SharedPreferences pref, int keyRessourceId, int value) {
        pref.edit().putInt(context.getString(keyRessourceId), value).apply();
    }

    private void setLong(SharedPreferences pref, int keyRessourceId, long value) {
        pref.edit().putLong(context.getString(keyRessourceId), value).apply();
    }

    private void setBool(SharedPreferences pref, int keyRessourceId, boolean value) {
        pref.edit().putBoolean(context.getString(keyRessourceId), value).apply();
    }

    private void setStringArray(SharedPreferences pref, int keyRessourceId, Object[] values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append("%%%");
            sb.append(value.toString());
        }
        setString(pref, keyRessourceId, sb.toString().replaceFirst("%%%", ""));
    }

    private String[] getStringArray(SharedPreferences pref, int keyRessourceId) {
        String value = pref.getString(context.getString(keyRessourceId), "%%%");
        if (value.equals("%%%")) {
            return new String[0];
        }
        return value.split("%%%");
    }

    private String getString(SharedPreferences pref, int ressourceId, String defaultValue) {
        return pref.getString(context.getString(ressourceId), defaultValue);
    }

    private String getString(SharedPreferences pref, int ressourceId, int ressourceIdDefaultValue) {
        return pref.getString(context.getString(ressourceId), context.getString(ressourceIdDefaultValue));
    }

    private boolean getBool(SharedPreferences pref, int ressourceId, boolean defaultValue) {
        return pref.getBoolean(context.getString(ressourceId), defaultValue);
    }

    private int getInt(SharedPreferences pref, int ressourceId, int defaultValue) {
        return pref.getInt(context.getString(ressourceId), defaultValue);
    }

    private long getLong(SharedPreferences pref, int ressourceId, long defaultValue) {
        return pref.getLong(context.getString(ressourceId), defaultValue);
    }


    public int getColor(SharedPreferences pref, String key, int defaultColor) {
        return pref.getInt(key, defaultColor);
    }

    public int getColorRes(@ColorRes int resColorId) {
        return ContextCompat.getColor(context, resColorId);
    }

    /*
    //     Setters & Getters
    */
    public boolean isAppFirstStart() {
        boolean value = getBool(prefApp, R.string.pref_key__app_first_start, true);
        setBool(prefApp, R.string.pref_key__app_first_start, false);
        return value;
    }

    public boolean isAppCurrentVersionFirstStart() {
        int value = getInt(prefApp, R.string.pref_key__app_first_start_current_version, -1);
        setInt(prefApp, R.string.pref_key__app_first_start_current_version, BuildConfig.VERSION_CODE);
        return value != BuildConfig.VERSION_CODE && !BuildConfig.IS_TEST_BUILD;
    }
}