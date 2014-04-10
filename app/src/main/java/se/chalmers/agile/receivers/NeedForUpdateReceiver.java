package se.chalmers.agile.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.egit.github.core.RepositoryCommit;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.MainActivity;
import se.chalmers.agile.tasks.OnPostExecuteCallback;
import se.chalmers.agile.tasks.UpdatesFetcher;
import se.chalmers.agile.utils.AppPreferences;

/**
 * Executed periodically, looks for new updates and, if found, launches a notification.
 */
public class NeedForUpdateReceiver extends BroadcastReceiver
        implements OnPostExecuteCallback<Collection<RepositoryCommit>> {

    public final static String ACTION = "START_ALARM";
    public static final int NOTIFICATION_ID = 10;
    public static final long UPDATE_TIME_MS = TimeUnit.SECONDS.toMillis(30);
    private final static String TAG = "UPDATE_FETCHING_TASK";
    private static AppPreferences prefs;
    public Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (prefs == null) {
            prefs = AppPreferences.getInstance();
        }
        if (intent != null && intent.getAction() != null
                && (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equals(ACTION))) {
            Log.d(TAG, "Starting update fetching service");
            startUpdatesService();
        } else {

            String[] branches = prefs.getBranches();
            if (branches.length == 0) return;
            Log.d(TAG+" SOMETHING", branches[branches.length - 1]);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] parts = branches[branches.length - 1].split(AppPreferences.REPO_BRANCH_SEPARATOR);
            String repoName = prefs.getRepositories()[prefs.getRepositories().length -1];
            String branch = parts[0];
            if (!repoName.isEmpty() && !branch.isEmpty()) {
                new UpdatesFetcher(context, this).execute(repoName, branch);
            } else {
                Log.d(this.getClass().toString(), "No branch was selected, no need to check for updates");
            }
        }
    }

    /**
     * Starts the periodic updates fetching.
     */
    private void startUpdatesService() {
        Log.d(TAG, "Starting automatic updates");
        Intent alarmIntent = new Intent(context, NeedForUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(), UPDATE_TIME_MS, pendingIntent);
    }

    @Override
    public void performAction(Collection<RepositoryCommit> data) {
        Toast.makeText(context, "CHECKING FOR UPDATES!", Toast.LENGTH_SHORT).show();
        buildAndLaunchNotification(data);
    }

    /**
     * Filters the commits to check for something new.
     *
     * @param data Latest commits.
     * @return A filtered list
     */
    private Collection<RepositoryCommit> filterData(Collection<RepositoryCommit> data) {
        Collection<RepositoryCommit> result = new LinkedList<RepositoryCommit>();
        long from = prefs.getLastUpdateTime();
        Date fromTime = new Date();
        fromTime.setTime(from);
        for (RepositoryCommit rc : data) {
            if (rc.getCommit().getCommitter().getDate().after(fromTime)) {
                result.add(rc);
            } else break;
        }
        return result;

    }

    /**
     * Launches a notification if there is something new.
     *
     * @param updates
     */
    private void buildAndLaunchNotification(Collection<RepositoryCommit> updates) {
        Collection<RepositoryCommit> news = filterData(updates);
        if (news.size() == 0) return;
        //TODO this has to be done on the commit list activity
        setLastUpdateDate(news.iterator().next());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.dogen)
                        .setContentTitle("New updates on X repository/branch blabla")
                        .setContentText(news.size() + " new commits!").setAutoCancel(true);

        //TODO and select the correct tab!
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Useful for keeping track of the last update's date. In this way, we can know if we actually
     * have news.
     */
    private void setLastUpdateDate(RepositoryCommit c) {
        long time = c.getCommit().getCommitter().getDate().getTime();
        prefs.setLastUpdateTime(time);
    }
}
