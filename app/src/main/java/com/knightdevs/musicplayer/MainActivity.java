package com.knightdevs.musicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongsAdapter.OnClickListener, MusicService.OnUpdateUIListener {

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private ImageView bottomSheetPlayPause;
    private TextView bottomSheetSongTitle, bottomSheetwSongArtist;
    private SharePreferenceClass prefs;
    private ViewPager viewPager;
    private TabLayout tabLayout;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllSongsFragment(), "Songs");
        adapter.addFragment(new AlbumFragment(), "Album");
        adapter.addFragment(new ArtistFragment(), "Artist");
        adapter.addFragment(new PlayListFragment(), "PlayList");
        viewPager.setAdapter(adapter);
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
        setupViewPager(viewPager);
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
//            musicSrv.setList(songList);
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
