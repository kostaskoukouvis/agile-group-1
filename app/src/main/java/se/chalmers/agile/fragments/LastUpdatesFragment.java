package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


    public static final String LAST_UPDATE_TIME = "last_update";


    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private String branchName;
    private String repositoryName;
    /**
     * Builds a instance once provided the correct parameters.
     *
     * @return
     */
    public static LastUpdatesFragment createInstance() {


        LastUpdatesFragment fragment = new LastUpdatesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        branchName = getBranchFromPreferences();
        repositoryName = getRepoFromPreferences();

        //Call the async task
        new UpdatesFetcher(getActivity().getApplicationContext(),this).execute(repositoryName, branchName);

    }


    @Override
    public void onResume() {
        super.onResume();
        String projectName = getRepoFromPreferences();
        String branchName = getBranchFromPreferences();
        new UpdatesFetcher(getActivity().getApplicationContext(), this).execute(projectName, branchName);
    }


    private String getBranchFromPreferences(){
        SharedPreferences sp = getActivity().getApplication().getApplicationContext().getSharedPreferences("Application",Context.MODE_PRIVATE);
        String str = sp.getString(BranchFragment.BRANCH_STR, "");
        String [] arr = str.split(BranchFragment.BRANCH_SEPARATOR);
        Log.d("BranchFtched", arr[arr.length - 1]);
        return arr[arr.length - 1];
    }

    private String getRepoFromPreferences(){
        SharedPreferences sp = getActivity().getApplication().getApplicationContext().getSharedPreferences("Application",Context.MODE_PRIVATE);
        String str = sp.getString(RepositoryFragment.REPOSITORY_STR, "");
        String [] arr = str.split(RepositoryFragment.REPOSITORY_SEPARATOR);
        Log.d("RepoFtched",arr[arr.length - 1] );
        return arr[arr.length - 1];

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
