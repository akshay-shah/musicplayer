package com.knightdevs.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> implements RecyclerView.OnItemTouchListener{

    private ArrayList<Song> songList;
    private Context context;
    private OnClickListener listener;

    public SongsAdapter(ArrayList<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
        listener = (OnClickListener)context;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSongTitle, textViewSongArtist,textViewPlayBackTIme;
        private CircleImageView imgViewCover;

        public SongViewHolder(View itemView) {
            super(itemView);
            textViewSongTitle = (TextView) itemView.findViewById(R.id.textViewSongTitle);
            textViewSongArtist = (TextView) itemView.findViewById(R.id.textViewSongArtist);
//            textViewPlayBackTIme = (TextView) itemView.findViewById(R.id.textViewPlayBackTIme);
            imgViewCover = (CircleImageView)itemView.findViewById(R.id.imgViewCover);
        }
    }

    @Override
    public SongsAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songview, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(v);
            }
        });
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.textViewSongTitle.setText(song.getTitle());
        holder.textViewSongArtist.setText(song.getArtistName());
        holder.imgViewCover.setTag(position);
//        String seconds = String.valueOf((song.getSongLength()  % 60000) / 1000);
//        if(seconds.length()==1){
//            seconds=seconds+"0";
//        }
//        String minutes = String.valueOf(song.getSongLength() / 60000);
//        holder.textViewPlayBackTIme.setText(""+minutes+":"+seconds);
//        if(song.getPath()!=null) {
//            Bitmap bm = BitmapFactory.decodeFile(song.getPath());
//            holder.imgViewCover.setImageBitmap(bm);
//        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public interface OnClickListener{
        public void onItemClickListener(View view);
    }
}
