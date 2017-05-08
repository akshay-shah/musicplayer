package com.knightdevs.musicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.knightdevs.musicplayer.pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements SongsAdapter.OnClickListener, MusicService.OnUpdateUIListener {

    private ArrayList<Song> songList;
    private RecyclerView recycleSongsView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private ImageView bottomSheetPlayPause;
    private TextView bottomSheetSongTitle, bottomSheetwSongArtist;
    private SharePreferenceClass prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK},
                    101);
            return;
        } else {
            setupDasboard();
        }

    }

    private void init() {
        songList = new ArrayList<>();
        recycleSongsView = (RecyclerView) findViewById(R.id.recycleSongsView);
        bottomSheetPlayPause = (ImageView) findViewById(R.id.bottomSheetPlayPause);
        bottomSheetSongTitle = (TextView) findViewById(R.id.bottomSheetSongTitle);
        bottomSheetwSongArtist = (TextView) findViewById(R.id.bottomSheetwSongArtist);
        bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetLayout);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv.isPlaying()) {
                    musicSrv.pauseSong();
                    bottomSheetPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_btn));
                } else {
                    musicSrv.startSong();
                    bottomSheetPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn));
                }
            }
        });
        prefs = new SharePreferenceClass(this);
        prefs.init();
        if(prefs.isInitialized()){
            bottomSheetSongTitle.setText(prefs.getTitleString());
            bottomSheetwSongArtist.setText(prefs.getArtistName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void setupDasboard() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri coverUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Cursor coverCursor = musicResolver.query(coverUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst() && coverCursor != null && coverCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
//            int coverColumn = coverCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                long thisAlbumId = musicCursor.getLong(albumId);
                songList.add(new Song(thisTitle, thisArtist, thisId, thisDuration, thisAlbumId, null));
            }
            while (musicCursor.moveToNext() && coverCursor.moveToNext());
        }
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

//        Thread th = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (Song s : songList) {
//                    Cursor cursor = MainActivity.this.managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                            new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
//                            MediaStore.Audio.Albums._ID + "=?",
//                            new String[]{String.valueOf(s.getAlbumId())},
//                            null);
//                    if (cursor.moveToFirst()) {
//                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//                        s.setPath(path);
//                    }
//                }
//            }
//        });
//        th.start();


        SongsAdapter adapter = new SongsAdapter(songList, MainActivity.this);
        recycleSongsView.setLayoutManager(new LinearLayoutManager(this));
        recycleSongsView.setItemAnimator(new DefaultItemAnimator());
        recycleSongsView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupDasboard();
            } else {
                // User refused to grant permission.
            }
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicSrv.setUiListener(MainActivity.this);
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onItemClickListener(View view) {
        View circularView = ((ViewGroup) view).getChildAt(0);
        musicSrv.setSong(Integer.parseInt(circularView.getTag().toString()));
        musicSrv.playSong();
        String[] songInfo = musicSrv.upDateBottomSheet();
        bottomSheetSongTitle.setText(songInfo[0]);
        bottomSheetwSongArtist.setText(songInfo[1]);
        bottomSheetPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn));
    }


    @Override
    protected void onDestroy() {
        musicSrv.saveSongs();
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    public void changeUI(String title, String artist) {
        bottomSheetSongTitle.setText(title);
        bottomSheetwSongArtist.setText(artist);
    }
}
