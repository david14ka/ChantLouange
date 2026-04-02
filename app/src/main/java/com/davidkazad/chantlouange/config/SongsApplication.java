package com.davidkazad.chantlouange.config;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;

public class SongsApplication extends Application {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    //public static boolean isPlaying = mediaPlayer.isPlaying();

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName("chant_louange")
                .build();

        // Apply dark mode by default
        boolean isNight = Prefs.getBoolean("night_mode", true);
        AppCompatDelegate.setDefaultNightMode(isNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Pré-chargement du cache de tous les livres en arrière-plan.
        // Chaque livre peut contenir 200-650 chansons (fichiers de données > 300KB),
        // donc on fait ce travail hors du thread principal pour ne pas bloquer le démarrage.
        new Thread(() -> {
            for (com.davidkazad.chantlouange.models.Book book : com.davidkazad.chantlouange.models.Book.bookList) {
                book.getPages(); // déclenche la construction du cache par défaut
                book.sort();     // déclenche la construction du cache alphabétique
            }
        }, "book-cache-preloader").start();
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
        super.attachBaseContext(base);
    }

    public long VersionCode(){
        long versionCode = 0;

        try {
            PackageInfo pInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = pInfo.getLongVersionCode();
            } else {
                versionCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public String versionName(){
        String versionName = "v3";

        try {
            PackageInfo pInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);

            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }
}
