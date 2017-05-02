package com.knightdevs.musicplayer.pojo;

/**
 * Created by ashah on 2/5/17.
 */

public class Song {
    String title;
    String artistName;
    long songId;
    long songLength;
    String path;
    long albumId;



    public Song(String title, String artistName, long songId, long songLength, long albumId, String path) {
        this.title = title;
        this.artistName = artistName;
        this.songId = songId;
        this.songLength = songLength;
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getSongLength() {
        return songLength;
    }

    public void setSongLength(long songLength) {
        this.songLength = songLength;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
