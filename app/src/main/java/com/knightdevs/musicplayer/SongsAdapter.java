package com.knightdevs.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.knightdevs.musicplayer.pojo.Song;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ashah on 2/5/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private ArrayList<Song> songList;
    private Context context;

    public SongsAdapter(ArrayList<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;

    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSongTitle, textViewSongArtist;
        private CircleImageView imgViewCover;

        public SongViewHolder(View itemView) {
            super(itemView);
            textViewSongTitle = (TextView) itemView.findViewById(R.id.textViewSongTitle);
            textViewSongArtist = (TextView) itemView.findViewById(R.id.textViewSongArtist);
            imgViewCover = (CircleImageView)itemView.findViewById(R.id.imgViewCover);
        }
    }

    @Override
    public SongsAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songview, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.textViewSongTitle.setText(song.getTitle());
        holder.textViewSongArtist.setText(song.getArtistName());
        if(song.getPath()!=null) {
            Bitmap bm = BitmapFactory.decodeFile(song.getPath());
            holder.imgViewCover.setImageBitmap(bm);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
