package com.knightdevs.musicplayer;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knightdevs.musicplayer.pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ashah on 9/5/17.
 */

public class AllSongsFragment extends Fragment implements SongsAdapter.OnClickListener {
    private ArrayList<Song> songList;
    private RecyclerView recycleSongsView;
    private MainActivityInterface updateActivityInterface;

    public AllSongsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateActivityInterface = (MainActivityInterface) getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsongs_frgament, null);
        recycleSongsView = (RecyclerView) v.findViewById(R.id.recycleSongsView);
        songList = new ArrayList<>();
        setupSongsView();
        return v;
    }


    private void setupSongsView() {
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri coverUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Cursor coverCursor = musicResolver.query(coverUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst() && coverCursor != null && coverCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
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
//        updateActivityInterface.setSongList(songList);
        SongsAdapter adapter = new SongsAdapter(songList, getActivity(), this);
        recycleSongsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleSongsView.setItemAnimator(new DefaultItemAnimator());
        recycleSongsView.setAdapter(adapter);

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

    }


    @Override
    public void onItemClickListener(View view) {
        View circularView = ((ViewGroup) view).getChildAt(0);
        updateActivityInterface.setSongList(songList);
        updateActivityInterface.setSong(Integer.parseInt(circularView.getTag().toString()));
        updateActivityInterface.playSong();
        updateActivityInterface.updateOnItemClick();
    }
}
