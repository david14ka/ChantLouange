package com.davidkazad.chantlouange.datas;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Base de données des versets bibliques tirés du livre des Psaumes.
 * Lit les versets dynamiquement depuis le fichier JSON situé dans les assets.
 */
public class PsalmVerses {
    private static final String TAG = "PsalmVerses";

    public static class Verse {
        public final String text;
        public final String reference;

        public Verse(String text, String reference) {
            this.text = text;
            this.reference = reference;
        }
    }

    private static List<Verse> VERSES = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean isLoaded = false;

    /**
     * Initialise la base de données de versets depuis le fichier assets/psaumes_lsg.json.
     * Doit être appelé une fois (par exemple, dans onCreate du fragment ou de l'activité).
     */
    public static void init(Context context) {
        if (isLoaded) return;
        
        try {
            InputStream is = context.getAssets().open("psaumes_lsg.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String text = obj.getString("text");
                String reference = obj.getString("reference");
                VERSES.add(new Verse(text, reference));
            }
            isLoaded = true;
            Log.d(TAG, "Chargé " + VERSES.size() + " versets des Psaumes.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des Psaumes depuis les assets", e);
            // Fallback en cas d'erreur
            if (VERSES.isEmpty()) {
                VERSES.add(new Verse("Que tout ce qui respire loue l'Éternel ! Louez l'Éternel !", "Psaume 150:6"));
            }
        }
    }

    /**
     * Retourne un verset aléatoire des Psaumes.
     */
    public static Verse getRandomVerse() {
        if (VERSES.isEmpty()) {
            return new Verse("L'Éternel est mon berger : je ne manquerai de rien.", "Psaume 23:1");
        }
        return VERSES.get(random.nextInt(VERSES.size()));
    }

    public static List<Verse> getAllVerses() {
        return VERSES;
    }
}
