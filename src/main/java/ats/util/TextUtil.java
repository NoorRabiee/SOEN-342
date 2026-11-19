package main.java.ats.util;

import java.text.Normalizer;

public class TextUtil {
    private TextUtil() {
    }

    // Lower-cases and strips accents
    // e.g., "Émilie" matches "emilie"
    public static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String n = Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase();
    }

    public static String digitsOnly(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * True when every whitespace-separated token of the query occurs in the text.
     */
    public static boolean containsAllTokens(String text, String query) {
        String t = normalize(text);
        for (String token : normalize(query).split("\\s+")) {
            if (!token.isEmpty() && !t.contains(token)) {
                return false;
            }
        }
        return true;
    }
}
