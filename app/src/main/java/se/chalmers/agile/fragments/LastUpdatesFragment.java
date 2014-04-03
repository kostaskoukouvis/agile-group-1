package se.chalmers.agile.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;

import java.text.DateFormat;
import java.util.Collection;


import java.util.LinkedList;
import java.util.List;

import se.chalmers.agile.R;

/**
 * Displays the last updates from the selected repositories.
 */
public class LastUpdatesFragment extends ListFragment {

    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance();

    /**
     * Builds a instance once provided the correct parameters.
     * @param projectName
     * @param branchName
     * @return
     */
    public static LastUpdatesFragment createInstance(String projectName, String branchName) {
        Bundle bundle = new Bundle();
        bundle.putString("project", projectName);
        bundle.putString("branch", branchName);
        LastUpdatesFragment fragment = new LastUpdatesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = this.getArguments();
        String projectName = extras.getString("project");
        String branchName = extras.getString("branch");
        new GetUpdatesTasks().execute(projectName, branchName);
    }

    /**
     * Adapter to show updates.
     */
    public class UpdatesAdapter extends ArrayAdapter<RepositoryCommit> {

        Context context;
        int layoutResourceId;
        RepositoryCommit data[] = null;

        public UpdatesAdapter(Context context, int layoutResourceId, RepositoryCommit[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }
            Commit commit = data[position].getCommit();

            TextView branch = (TextView) row.findViewById(R.id.commit_branch);
            branch.setText("~");

            TextView date = (TextView) row.findViewById(R.id.commit_date);
            date.setText(dateFormat.format(commit.getCommitter().getDate()));

            TextView message = (TextView) row.findViewById(R.id.commit_message);
            message.setText(commit.getMessage());

            TextView author = (TextView) row.findViewById(R.id.commit_user);
            author.setText(commit.getCommitter().getName());

            return row;
        }
    }

    /**
     * Performs the commit fetching in background.
     */
    private class GetUpdatesTasks extends AsyncTask<String, Void, Collection<RepositoryCommit>> {
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
            setListAdapter(new UpdatesAdapter(getActivity(),
                    R.layout.updates_list_item_layout, commits.toArray(new RepositoryCommit[commits.size()])));
        }
    }
}
