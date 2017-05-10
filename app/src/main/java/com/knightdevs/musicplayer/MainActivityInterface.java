package com.knightdevs.musicplayer;

import com.knightdevs.musicplayer.pojo.Song;

import java.util.ArrayList;

/**
 * Created by ashah on 10/5/17.
 */

public interface MainActivityInterface {
    public void setSong(int songId);
    public void playSong();
    public void setSongList(ArrayList<Song> songsList);
    public void updateOnItemClick();
}
