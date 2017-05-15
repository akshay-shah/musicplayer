package com.knightdevs.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * Created by ashah on 8/5/17.
 */

public class SharePreferenceClass {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    SharePreferenceClass(Context context) {
        this.context = context;
    }

    public void init() {
        preferences = context.getSharedPreferences("MusicApp", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void putTitleString(String value) {
        editor.putString("SongTitle", value);
        editor.commit();
    }

    public void putArtistName(String value) {
        editor.putString("SongArtist", value);
        editor.commit();
    }

    public void putSongID(long value) {
        editor.putLong("SongID", value);
        editor.commit();
    }

    public String getTitleString() {
        String value = preferences.getString("SongTitle", "null");
        return value;
    }

    public String getArtistName() {
        String value = preferences.getString("SongArtist", "null");
        return value;
    }

    public long getSongID() {
        long value = preferences.getLong("SongID", 0);
        return value;
    }

    public void putAlbumId(long value) {
        editor.putLong("AlbumId", value);
        editor.commit();
    }

    public long getAlbumId() {
        long value = preferences.getLong("AlbumId", 0);
        return value;
    }

    public boolean isInitialized() {
        if (preferences.contains("SongTitle")) {
            return true;
        } else
            return false;

    }


}
