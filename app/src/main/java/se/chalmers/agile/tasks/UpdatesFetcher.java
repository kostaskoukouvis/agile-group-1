package se.chalmers.agile.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import se.chalmers.agile.R;

/**
 * Performs the commit fetching in background.
 */
public class UpdatesFetcher extends AsyncTask<String, Void, Collection<RepositoryCommit>> {

    private final static String TAG = "UPDATES_FETCHER";
    private Context context;
    private OnPostExecuteCallback<Collection<RepositoryCommit>> callback;

    public UpdatesFetcher(Context context, OnPostExecuteCallback<Collection<RepositoryCommit>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Collection<RepositoryCommit> doInBackground(String... args) {
        CommitService cs = new CommitService();

        cs.getClient().setOAuth2Token(context.getString(R.string.api_key));


        String[] project = args[0].split("/");

        IRepositoryIdProvider repositoryId = RepositoryId.create(project[0], project[1]);
        PageIterator<RepositoryCommit> commitPages = cs.pageCommits(repositoryId, args[1], null, 10);
        /*try {
            for (RepositoryCommit rc : cs.getCommits(repositoryId))
                Log.d("TEST", ""+(cs.getCommit(repositoryId, rc.getSha()).getFiles()==null));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (commitPages.hasNext()) {
            return commitPages.next();
        } else {
            return new LinkedList<RepositoryCommit>();
        }
    }

    @Override
    protected void onPostExecute(Collection<RepositoryCommit> commits) {
        super.onPostExecute(commits);
        callback.performAction(commits);
    }


}