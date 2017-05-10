package com.knightdevs.musicplayer;

/**
 * Created by ashah on 10/5/17.
 */

public interface MainActivityContract {
    interface View extends BaseView<Presenter> {
        public void handlePlayPause();
        public void updatePlayingSong();
    }

    interface Presenter extends BasePresenter {

    }
}
