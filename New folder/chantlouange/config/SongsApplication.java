package com.davidkazad.chantlouange.config;

import android.app.Application;

import com.davidkz.eazyorm.ActiveAndroid;
import com.pixplicity.easyprefs.library.Prefs;

public class SongsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

       /* ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(0))
                        .cacheOnDisk(true)
                        .build())
                .build();

        ImageLoader.getInstance().init(imageLoaderConfiguration);*/

        ActiveAndroid.initialize(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName("chant_louange")
                .build();
    }
}
