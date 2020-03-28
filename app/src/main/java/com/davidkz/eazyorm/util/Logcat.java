package com.davidkz.eazyorm.util;

import android.util.Log;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;

/**
 * Created by Espérance on 22/01/2018.
 */
@Table(name = "logcat")
public final class Logcat extends Model {


    @Column(name = "type") public String type;
    @Column(name = "tag") public String TAG;
    @Column(name = "msg") public String msg;

    public static boolean isEnabled = true;

    public Logcat() {

    }

    public Logcat(String type, String TAG, String msg) {

        this.type = type;
        this.TAG = TAG;
        this.msg = msg;
    }

    private static void LogSave(String tag, String type, String msg) {
        Logcat logcat = new Logcat(type,tag,msg);
        logcat.save();
    }


    public static int d(String TAG, String msg){
        if (isEnabled){
            LogSave(TAG,"d",msg);
            return Log.d(TAG, "d: "+msg);
        }
        return 0;
    }

    public static int e(String TAG, String msg){
        if (isEnabled){
            LogSave(TAG,"e",msg);
            return Log.e(TAG, "d: "+msg);
        }
        return 0;
    }
    public static int i(String TAG, String msg){
        if (isEnabled){
            LogSave(TAG,"i",msg);
            return Log.d(TAG, "i: "+msg);
        }
        return 0;

    }



}
