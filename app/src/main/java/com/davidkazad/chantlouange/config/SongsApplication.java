package com.davidkazad.chantlouange.config;

import android.app.Application;
import android.content.Context;

import com.davidkazad.chantlouange.R;
import com.davidkz.eazyorm.ActiveAndroid;
import com.karumi.dexter.Dexter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;

public class SongsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*Dexter.withActivity(activity)
                .withPermission(permission)
                .withListener(listener)
                .check();*/
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



    @Override
    protected void attachBaseContext(Context base) {
        //MultiDex.install(this);
        super.attachBaseContext(base);
    }
}
