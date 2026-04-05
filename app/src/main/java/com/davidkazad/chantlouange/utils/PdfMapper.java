package com.davidkazad.chantlouange.utils;

import android.content.Context;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.util.Log;

public class PdfMapper {

    private static final String TAG = "PdfMapper";
    private static Map<String, String> UNIFIED_PDF_MAP;

    private static void init(Context context) {
        if (UNIFIED_PDF_MAP != null) return;
        UNIFIED_PDF_MAP = new HashMap<>();
        try {
            InputStream is = context.getResources().openRawResource(com.davidkazad.chantlouange.R.raw.pdf_map);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject pdfs = obj.getJSONObject(key);
                String pdfUrl = null;
                // Prefer A4
                if (pdfs.has("pdf_a4") && !pdfs.isNull("pdf_a4")) {
                    pdfUrl = pdfs.getString("pdf_a4");
                } else if (pdfs.has("pdf_a5") && !pdfs.isNull("pdf_a5")) {
                    pdfUrl = pdfs.getString("pdf_a5");
                }
                if (pdfUrl != null) {
                    UNIFIED_PDF_MAP.put(key, pdfUrl);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load pdf_map.json", e);
        }
    }

    public static String getPdfUrl(Context context, int bookId, String number) {
        init(context);
        String baseKey = number.trim().replaceAll("\\.\\s*$", "").trim().toLowerCase();
        String key = bookId + "_" + baseKey;
        Log.d(TAG, "Requesting PDF for key: " + key);
        return UNIFIED_PDF_MAP.get(key);
    }

    public static boolean hasPdf(Context context, int bookId, String number) {
        return getPdfUrl(context, bookId, number) != null;
    }
}
