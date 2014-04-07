package se.chalmers.agile.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.egit.github.core.RepositoryCommit;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.ContainerActivity;
import se.chalmers.agile.fragments.LastUpdatesFragment;
import se.chalmers.agile.tasks.OnPostExecuteCallback;
import se.chalmers.agile.tasks.UpdatesFetcher;

/**
 * Executed periodically, looks for new updates and, if found, launches a notification.
 */
public class NeedForUpdateReceiver extends BroadcastReceiver
        implements OnPostExecuteCallback<Collection<RepositoryCommit>> {
    public Context context;
    private SharedPreferences prefs;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.prefs = context.getSharedPreferences("Application",
                Context.MODE_PRIVATE);
        Log.d("TESTRECEIVER", "LAUNCHED!");

        new UpdatesFetcher(context, this).execute("SantiMunin/mockrepo", "master");
    }

    @Override
    public void performAction(Collection<RepositoryCommit> data) {
        Toast.makeText(context, "CHECKING!", Toast.LENGTH_SHORT).show();
        buildAndLaunchNotification(data);
    }

    /**
     * Filters the commits to check for something new.
     *
     * @param data Latest commits.
     * @return A filtered list.
     */
    private Collection<RepositoryCommit> filterData(Collection<RepositoryCommit> data) {
        Collection<RepositoryCommit> result = new LinkedList<RepositoryCommit>();
        long from = prefs.getLong(LastUpdatesFragment.LAST_UPDATE_TIME, 0);
        Date fromTime = new Date();
        fromTime.setTime(from);
        Log.d("NEWEST_COMMIT_TIME", "long: " + data.iterator().next().getCommit().getCommitter().getDate().getTime());
        Log.d("NEWEST_UPDATE_TIME", "long: " + from);
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
        setLastUpdateDate(news.iterator().next());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.dogen)
                        .setContentTitle("New updates on X repository/branch blabla")
                        .setContentText(news.size() + " new commits!");

        //TODO and select the correct tab!
        Intent resultIntent = new Intent(context, ContainerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ContainerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * Useful for keeping track of the last update's date. In this way, we can know if we actually
     * have news.
     */
    private void setLastUpdateDate(RepositoryCommit c) {
        long time = c.getCommit().getCommitter().getDate().getTime();
        SharedPreferences prefs =
                context.getSharedPreferences("Application", Context.MODE_PRIVATE);
        prefs.edit().putLong(LastUpdatesFragment.LAST_UPDATE_TIME, time).commit();
    }
}
