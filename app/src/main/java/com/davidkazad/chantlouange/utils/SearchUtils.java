package com.davidkazad.chantlouange.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SearchUtils {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9 ]");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Normalizes a string: removes accents, lowercases, and strips non-alphanumeric characters.
     */
    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD);
        n = DIACRITICS.matcher(n).replaceAll("");
        return NON_ALPHANUMERIC.matcher(n).replaceAll(" ").replaceAll("\\s+", " ").trim();
    }

    /**
     * Calculates a matching score from 0.0 to 1.0.
     * High performance version with contains short-circuit and length filtering.
     */
    public static double getFuzzyScore(String query, String target) {
        if (query == null || target == null || query.isEmpty()) return 0.0;
        
        String q = normalize(query);
        String t = normalize(target);
        
        if (q.equals(t)) return 1.0;
        
        // Exact Contains (High Priority Short-circuit)
        if (t.contains(q)) {
            if (t.startsWith(q)) return 0.95;
            return 0.85;
        }

        // If target is very long (e.g. lyrics), don't perform heavy tokenized fuzzy 
        // to prevent ANRs. For lyrics, literal contains is usually enough and preferred.
        if (t.length() > 500) {
             return 0.0; // Already tried contains above.
        }

        // Tokenized fuzzy matching (Title / Number only)
        String[] qTokens = q.split(" ");
        String[] tTokens = t.split(" ");

        double totalScore = 0;
        int matches = 0;

        for (String qToken : qTokens) {
            if (qToken.length() < 2) continue; // Skip single char fuzzy checks

            double bestTokenScore = 0;
            for (String tToken : tTokens) {
                double score = tokenMatchScore(qToken, tToken);
                if (score > bestTokenScore) bestTokenScore = score;
                if (bestTokenScore >= 1.0) break; // Found exact match for this token
            }
            
            if (bestTokenScore > 0.6) { // Increased threshold for noise reduction
                totalScore += bestTokenScore;
                matches++;
            }
        }

        if (matches == 0) return 0.0;

        double finalScore = (totalScore / qTokens.length) * ((double) matches / qTokens.length);
        return Math.min(finalScore, 0.84);
    }

    private static double tokenMatchScore(String q, String t) {
        if (q.equals(t)) return 1.0;
        if (t.startsWith(q)) return 0.95;
        if (t.contains(q)) return 0.85;

        // Typo filtering: skip Levenshtein if length difference is too large.
        if (Math.abs(q.length() - t.length()) > 2) return 0.0;
        
        // Skip for very short words
        if (q.length() < 3) return 0.0;

        int distance = levenshtein(q, t);
        int maxLen = Math.max(q.length(), t.length());
        
        if (maxLen == 0) return 0.0;
        double similarity = 1.0 - ((double) distance / maxLen);
        
        return similarity > 0.75 ? similarity : 0.0;
    }

    /**
     * Space-efficient Levenshtein: O(M) space instead of O(N*M).
     */
    public static int levenshtein(String s1, String s2) {
        if (s1.equals(s2)) return 0;
        if (s1.length() == 0) return s2.length();
        if (s2.length() == 0) return s1.length();

        int[] prevRow = new int[s2.length() + 1];
        int[] currRow = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) prevRow[j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            currRow[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                currRow[j] = Math.min(Math.min(
                    currRow[j - 1] + 1,      // Insertion
                    prevRow[j] + 1),         // Deletion
                    prevRow[j - 1] + cost);  // Substitution
            }
            System.arraycopy(currRow, 0, prevRow, 0, currRow.length);
        }
        return prevRow[s2.length()];
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}

