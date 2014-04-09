package se.chalmers.agile.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class useful to retrieve and save data used through all the app.
 */
public class AppPreferences {

    private SharedPreferences prefs;
    private final static String TAG = "Application preferences";
    private final static String USER_TAG = "user";
    private final static String PW_TAG = "pw";
    private final static String BRANCHES_TAG = "branches";
    public final static String BRANCHES_SEPARATOR = "#";
    public final static String REPO_BRANCH_SEPARATOR = "|";
    private final static String LAST_UPDATE_TIME = "last_update_time";


    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }


    public void setUser(String user) {
        if (user == null || user.isEmpty()) {
            throw new Error("Wrong argument: user cannot be null or empty.");
        }
        prefs.edit().putString(USER_TAG, user).commit();
    }

    public void setPassword(String pw) {
        if (pw == null || pw.isEmpty()) {
            throw new Error("Wrong argument: password cannot be null or empty.");
        }
        prefs.edit().putString(USER_TAG, pw).commit();
    }

    public String[] getBranches() {
        String raw = prefs.getString(BRANCHES_TAG, "");
        if (raw.isEmpty()) {
            return new String[0];
        }
        return raw.split(BRANCHES_SEPARATOR);
    }

    public String getUser() {
        return prefs.getString(USER_TAG, "");
    }

    public String getPassword() {
        return prefs.getString(PW_TAG, "");
    }

    public void appendBranch(String repo, String branch) {
        if (repo == null || repo.isEmpty() || branch == null || branch.isEmpty()) {
            throw new Error("Repository and branches cannot be null or empty");
        }
        StringBuilder builder = new StringBuilder(prefs.getString(BRANCHES_TAG, ""));
        builder.append(BRANCHES_SEPARATOR);
        builder.append(repo);
        builder.append(REPO_BRANCH_SEPARATOR);
        builder.append(branch);
        prefs.edit().putString(BRANCHES_TAG, builder.toString()).commit();
    }

    public void clearBranches() {
        prefs.edit().putString(BRANCHES_TAG, "").commit();
    }

    public void setLastUpdateTime(long time) {
        prefs.edit().putLong(LAST_UPDATE_TIME, time).commit();
    }

    public long getLastUpdateTime() {
        return prefs.getLong(LAST_UPDATE_TIME, 0);
    }
}
