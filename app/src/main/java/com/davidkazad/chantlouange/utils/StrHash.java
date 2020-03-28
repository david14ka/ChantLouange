package com.davidkazad.chantlouange.utils;

import com.davidkazad.chantlouange.models.Favoris;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class StrHash {

    public static void main(String[] args){
        addItem();
    }

    private static ArrayList<Favoris> getList() {
        String s  ="[{\"songsBook\":3,\"songsId\":5},{\"songsBook\":2,\"songsId\":1}]";
        Type listType = new TypeToken<List<Favoris>>() {}.getType();
        return new GsonBuilder().create().fromJson(s, listType);
    }
    private static ArrayList<Favoris> getHash() {
        String s  ="[{\"songsBook\":3,\"songsId\":5},{\"songsBook\":2,\"songsId\":1}]";
        Type listType = new TypeToken<List<Favoris>>() {}.getType();
        return new GsonBuilder().create().fromJson(s, listType);
    }
    private static boolean addItem() {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC,Modifier.FINAL,Modifier.ABSTRACT).create();
        String j = gson.toJson(getList());
        //
        System.out.println(j);
        return true;
    }


}
