package se.chalmers.agile.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;

import java.util.Collection;

import se.chalmers.agile.R;

/**
 * Performs the commit fetching in background.
 */
public class UpdatesFetcher extends AsyncTask<String, Void, Collection<RepositoryCommit>> {

    private Context context;
    private OnPostExecuteCallback<Collection<RepositoryCommit>> callback;

    public UpdatesFetcher(Context context, OnPostExecuteCallback<Collection<RepositoryCommit>> callback) {
        this.context = context;
        this.callback = callback;
    }


    @Override
    protected Collection<RepositoryCommit> doInBackground(String... args) {
        CommitService cs = new CommitService();
        String[] project = args[0].split("/");
        IRepositoryIdProvider repositoryId = RepositoryId.create(project[0], project[1]);
        PageIterator<RepositoryCommit> commitPages = cs.pageCommits(repositoryId, args[1], null, 10);
        return commitPages.next();
    }

    @Override
    protected void onPostExecute(Collection<RepositoryCommit> commits) {
        super.onPostExecute(commits);
        if (commits.size() > 0) {
            setLastUpdateDate(commits.iterator().next().getCommit().getCommitter().getDate().getTime());
        }
        callback.performAction(commits);
    }

    /**
     * Useful for keeping track of the last update's date. In this way, we can know if we actually
     * have news.
     *
     * @param time Time of the last fetched update.
     */
    private void setLastUpdateDate(long time) {
        SharedPreferences prefs =
                context.getSharedPreferences("Application", Context.MODE_PRIVATE);
        prefs.edit().putLong("lastUpdate", time).commit();
    }
}