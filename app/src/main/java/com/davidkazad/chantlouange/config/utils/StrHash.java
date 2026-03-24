package com.davidkazad.chantlouange.config.utils;

import com.davidkazad.chantlouange.models.Favorites;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StrHash {

    public static void main(String[] args){
        addItem();
    }

    private static ArrayList<Favorites> getList() {
        String s  ="[{\"songsBook\":3,\"songsId\":5},{\"songsBook\":2,\"songsId\":1}]";
        Type listType = new TypeToken<List<Favorites>>() {}.getType();
        return new GsonBuilder().create().fromJson(s, listType);
    }
    private static ArrayList<Favorites> getHash() {
        String s  ="[{\"songsBook\":3,\"songsId\":5},{\"songsBook\":2,\"songsId\":1}]";
        Type listType = new TypeToken<List<Favorites>>() {}.getType();
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
