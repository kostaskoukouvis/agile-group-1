package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.RepositoryCommit;

import java.text.DateFormat;
import java.util.Collection;

import se.chalmers.agile.R;
import se.chalmers.agile.tasks.OnPostExecuteCallback;
import se.chalmers.agile.tasks.UpdatesFetcher;

/**
 * Displays the last updates from the selected repositories.
 */
public class LastUpdatesFragment extends ListFragment
        implements OnPostExecuteCallback<Collection<RepositoryCommit>> {

    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance();
    public static final String LAST_UPDATE_TIME = "last_update";

    /**
     * Builds a instance once provided the correct parameters.
     *
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
        new UpdatesFetcher(getActivity().getApplicationContext(), this).execute(projectName, branchName);
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
     * Just adapts the adapter.
     *
     * @param commits Items of the list.
     */
    @Override
    public void performAction(Collection<RepositoryCommit> commits) {
        setListAdapter(new UpdatesAdapter(getActivity(),
                R.layout.updates_list_item_layout,
                commits.toArray(new RepositoryCommit[commits.size()])));
    }
}
