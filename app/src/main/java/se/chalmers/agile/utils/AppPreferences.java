package se.chalmers.agile.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class useful to retrieve and save data used through all the app.
 */
public class AppPreferences extends Application {

    public final static String BRANCHES_SEPARATOR = "###";
    public final static String REPO_BRANCH_SEPARATOR = "/";
    private final static String TAG = "Application preferences";
    private final static String USER_TAG = "user";
    private final static String PW_TAG = "pw";
    //REPOSITORY CONSTANTS
    private final static String REPO_TAG = "repo";
    private final static String REPO_SEPARATOR = "###";

    //BRANCHES CONSTANTS
    private final static String BRANCHES_TAG = "branch";
    private final static String LAST_UPDATE_TIME = "last_update_time";
    private static AppPreferences appPrefs;
    private SharedPreferences readOnlyPrefs;
    private SharedPreferences.Editor writeablePrefs;


    public AppPreferences() {
        super();
        appPrefs = this;
    }

    public static AppPreferences getInstance() {
        return appPrefs;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readOnlyPrefs = getBaseContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        writeablePrefs = readOnlyPrefs.edit();
    }

    public String[] getBranches() {
        String raw = readOnlyPrefs.getString(BRANCHES_TAG, "");
        if (raw.isEmpty()) {
            return new String[0];
        }
        return raw.split(BRANCHES_SEPARATOR);
    }

    public boolean logOut(){
        return writeablePrefs.clear().commit();
    }

    public String getUser() {
        return readOnlyPrefs.getString(USER_TAG, "");
    }

    public void setUser(String user) {
        if (user == null || user.isEmpty()) {
            throw new Error("Wrong argument: user cannot be null or empty.");
        }
        writeablePrefs.putString(USER_TAG, user).commit();
    }

    public String getPassword() {
        return readOnlyPrefs.getString(PW_TAG, "");
    }

    public void setPassword(String pw) {
        if (pw == null || pw.isEmpty()) {
            throw new Error("Wrong argument: password cannot be null or empty.");
        }
        writeablePrefs.putString(PW_TAG, pw).commit();
    }

    public void appendRepository(String repoName) {
        StringBuilder builder = new StringBuilder(readOnlyPrefs.getString(REPO_TAG, ""));
        builder.append(REPO_SEPARATOR);
        builder.append(repoName);
        writeablePrefs.putString(REPO_TAG, builder.toString()).commit();
    }

    public void clearRepository(){
        writeablePrefs.putString(REPO_TAG, "").commit();
    }

    public String[]  getRepositories(){
        String raw = readOnlyPrefs.getString(REPO_TAG, "");
        if (raw.isEmpty()) {
            return new String[0];
        }
        return raw.split(REPO_SEPARATOR);
    }


    public void appendBranch(String repo, String branch) {
        if (repo == null || repo.isEmpty() || branch == null || branch.isEmpty()) {
            throw new Error("Repository and branches cannot be null or empty");
        }
        StringBuilder builder = new StringBuilder(readOnlyPrefs.getString(BRANCHES_TAG, ""));
       /* builder.append(repo);
        builder.append(REPO_BRANCH_SEPARATOR);*/
        builder.append(branch);
        builder.append(BRANCHES_SEPARATOR);
        writeablePrefs.putString(BRANCHES_TAG, builder.toString()).commit();
    }

    public void clearBranches() {
        writeablePrefs.putString(BRANCHES_TAG, "").commit();
    }

    public long getLastUpdateTime() {
        return readOnlyPrefs.getLong(LAST_UPDATE_TIME, 0);
    }

    public void setLastUpdateTime(long time) {
        readOnlyPrefs.edit().putLong(LAST_UPDATE_TIME, time).commit();
    }
}
