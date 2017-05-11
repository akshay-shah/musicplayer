package com.knightdevs.musicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.knightdevs.musicplayer.pojo.Song;

import java.util.ArrayList;

/**
 * Created by ashah on 4/5/17.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn = 0;
    private final IBinder musicBind = new MusicBinder();
    private static boolean isPlaying = false;
    public OnUpdateUIListener uiListener;
    private SharePreferenceClass prefs;
    private Equalizer mEqualizer;

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
        prefs = new SharePreferenceClass(this);
        prefs.init();
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        mEqualizer = new Equalizer(0, player.getAudioSessionId());
        short bands = mEqualizer.getNumberOfBands();
        Log.d("MusicApp", "Bands =" + bands);
        short[] range = mEqualizer.getBandLevelRange();
        for (int i = 0; i < range.length; i++) {
            Log.d("MusicApp", "range =" + range[i]);
        }
        Log.d("MusicApp", "Presets =" + mEqualizer.getNumberOfPresets());
//        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
//            Log.d("MusicApp", "Presets =" + mEqualizer.getPresetName(i));
//            mEqualizer.usePreset(i);
//            Log.d("MusicApp", "BandLevelRangeLower =" + mEqualizer.getBandLevelRange()[0]);
//            Log.d("MusicApp", "BandLevelRangeUpper =" + mEqualizer.getBandLevelRange()[1]);
//            short bands1 = mEqualizer.getNumberOfBands();
//            Log.d("MusicApp", "Bands1 =" + bands);
//            mEqualizer.setEnabled(true);
//        }
        Log.d("MusicApp", "Presets =" + mEqualizer.getPresetName((short)6));
        mEqualizer.usePreset((short)6);
        Log.d("MusicApp", "BandLevelRangeLower =" + mEqualizer.getBandLevelRange()[0]);
        Log.d("MusicApp", "BandLevelRangeUpper =" + mEqualizer.getBandLevelRange()[1]);
        short bands1 = mEqualizer.getNumberOfBands();
        Log.d("MusicApp", "Bands1 =" + bands);
        mEqualizer.setEnabled(true);
        Log.d("MusicApp", "Current Preset =" + mEqualizer.getCurrentPreset());;

    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setSong(songPosn + 1);
        songPosn = songPosn + 1;
        playSong();
        String[] info = upDateBottomSheet();
        uiListener.changeUI(info[0], info[1]);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public String[] upDateBottomSheet() {
        Song playSong = songs.get(songPosn);
        String title = playSong.getTitle();
        String artist = playSong.getArtistName();
        return new String[]{title, artist};
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setUiListener(OnUpdateUIListener listener) {
        this.uiListener = listener;
    }

    public void playSong() {
        player.reset();
        try {
            Song playSong = songs.get(songPosn);
            long currSong = playSong.getSongId();
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            isPlaying = true;
            player.prepareAsync();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void playSongbyId(long songId, String title, String artist) {
        player.reset();
        long currSong = songId;
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        isPlaying = true;
        player.prepareAsync();
        uiListener.changeUI(title, artist);
    }

    public void pauseSong() {
        isPlaying = false;
        player.pause();
    }

    public void startSong() {
        if (!isPlaying()) {
            player.start();
            isPlaying = true;
        }
    }

    public boolean seekTime() {
        Log.d("MusicService", "SeekTIme" + player.getCurrentPosition());
        if (player.getCurrentPosition() == 0) {
            return true;
        } else
            return false;
    }

    public void saveSongs() {
        Song playSong = songs.get(songPosn);
        prefs.putTitleString(playSong.getTitle());
        prefs.putArtistName(playSong.getArtistName());
        prefs.putSongID(playSong.getSongId());
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public interface OnUpdateUIListener {
        void changeUI(String title, String artist);
    }

}
