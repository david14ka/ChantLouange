package com.davidkazad.chantlouange.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.davidkazad.chantlouange.utils.Constants.TAG;

public class StringUtil {

    public static String listToString(List data){
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.ABSTRACT).create();
        String l = gson.toJson(data);
        Log.d(TAG, "getList: " + l);
        return l;
    }

    public static String getKey(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hms");

        long mill = Long.parseLong("1500433611144");
        return format.format(new Date());//+"-"+mill;
    }
}
