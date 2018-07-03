package iyd2.projects.itunessearchapi;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Class for working with SharedPreferences.
 */
public class QueryPreferences {
    private static final String TAG = "QueryPreferences";
    private static final String SEARCH_QUERY = "search_query";

    /**
     * Returns last search query.
     * "Michal Jackson" on null.
     */
    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SEARCH_QUERY, "Michal Jackson");
    }

    /**
     * Set last search query.
     */
    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SEARCH_QUERY, query)
                .apply();

    }
}
