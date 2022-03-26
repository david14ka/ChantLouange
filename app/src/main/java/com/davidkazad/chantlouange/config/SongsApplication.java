package com.davidkazad.chantlouange.config;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.davidkazad.chantlouange.R;
import com.davidkz.eazyorm.ActiveAndroid;
import com.karumi.dexter.Dexter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;

public class SongsApplication extends Application {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    //public static boolean isPlaying = mediaPlayer.isPlaying();

    @Override
    public void onCreate() {
        super.onCreate();
        /*Dexter.withActivity(activity)
                .withPermission(permission)
                .withListener(listener)
                .check();*/
        //setMediaPlayer();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(10))
                        .showImageOnLoading(R.drawable.ic_loarding)
                        .cacheOnDisk(true)
                        .build())
                .build();

        ImageLoader.getInstance().init(imageLoaderConfiguration);

        ActiveAndroid.initialize(this);
        //Post.deleteAll(Post.class);

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName("chant_louange")
                .build();
    }

    public static DisplayImageOptions displayImageCircleOptions;
    static {

         displayImageCircleOptions = new DisplayImageOptions.Builder()
                .displayer(new CircleBitmapDisplayer())
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_loarding)
                .showImageForEmptyUri(R.drawable.user)
                 .showImageOnFail(R.drawable.user)
                .build();
    }

    public void setMediaPlayer() {

        String url = "http://sense.alwaysdata.net/introduction.wma"; // your URL here
        url = "https://sense.alwaysdata.net/Amazing.mp3"; // your URL here

        //mediaPlayer = new MediaPlayer();
        SongsApplication.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            SongsApplication.mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            SongsApplication.mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        //MultiDex.install(this);
        super.attachBaseContext(base);
    }
}
