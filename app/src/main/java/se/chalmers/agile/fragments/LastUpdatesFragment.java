package se.chalmers.agile.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;

import java.text.SimpleDateFormat;
import java.util.Collection;

import se.chalmers.agile.R;

/**
 * Displays the last updates from the selected repositories.
 */
public class LastUpdatesFragment extends ListFragment {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1. Get the list of project/branches
        //List<ProjectBranch> branches = ...;
        //2. Fetch data from GitHub

        CommitService cs = new CommitService();
        IRepositoryIdProvider repositoryId = RepositoryId.create("marcyb5st", "agile-group-1");
        PageIterator<RepositoryCommit> commitPages = cs.pageCommits(repositoryId, "testingEnvironment", null, 10);
        Collection<RepositoryCommit> commits = commitPages.next();

        //3. Filtering

        //4. Display results
        // TODO: Change Adapter to display your content
        RepositoryCommit[] commitArray = new RepositoryCommit[commits.size()];
        commits.toArray(commitArray);
        setListAdapter(new UpdatesAdapter(getActivity(),R.layout.updates_list_item_layout, commitArray));
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
}
