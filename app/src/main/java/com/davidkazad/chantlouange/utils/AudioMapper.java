package com.davidkazad.chantlouange.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps CV song numbers to their streaming audio URLs.
 * Source: https://cantiques.yapper.fr/player/?filter=CV
 */
public class AudioMapper {

    private static final String BASE_URL = "https://cantiques.yapper.fr/media/";
    public static final int CC_BOOK_ID = 1;
    public static final int CV_BOOK_ID = 2;
    public static final int CS_BOOK_ID = 8;

    private static Map<String, List<String>> UNIFIED_AUDIO_MAP;

    private static void init(android.content.Context context) {
        if (UNIFIED_AUDIO_MAP != null) return;
        UNIFIED_AUDIO_MAP = new HashMap<>();
        try {
            java.io.InputStream is = context.getResources().openRawResource(com.davidkazad.chantlouange.R.raw.audio_map);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            org.json.JSONObject obj = new org.json.JSONObject(json);
            java.util.Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                org.json.JSONArray array = obj.getJSONArray(key);
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    urls.add(array.getString(i));
                }
                UNIFIED_AUDIO_MAP.put(key, urls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAudioUrls(android.content.Context context, int bookId, String number) {
        init(context);
        List<String> result = new ArrayList<>();
        
        String baseKey = number.trim().replaceAll("\\.\\s*$", "").trim().toLowerCase();
        String key = bookId + "_" + baseKey;
        
        if (UNIFIED_AUDIO_MAP.containsKey(key)) {
            for (String url : UNIFIED_AUDIO_MAP.get(key)) {
                if (url.startsWith("http")) {
                    result.add(url);
                } else {
                    result.add(BASE_URL + url);
                }
            }
        }

        return result;
    }

    public static boolean hasAudio(android.content.Context context, int bookId, String number) {
        return !getAudioUrls(context, bookId, number).isEmpty();
    }
}
