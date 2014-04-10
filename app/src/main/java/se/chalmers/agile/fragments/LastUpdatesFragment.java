package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.RepositoryCommit;

import java.text.DateFormat;
import java.util.Collection;

import se.chalmers.agile.R;
import se.chalmers.agile.tasks.OnPostExecuteCallback;
import se.chalmers.agile.tasks.UpdatesFetcher;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import se.chalmers.agile.utils.AppPreferences;


/**
 * Displays the last updates from the selected repositories.
 */
public class LastUpdatesFragment extends ListFragment
        implements OnPostExecuteCallback<Collection<RepositoryCommit>> {


    public static final String LAST_UPDATE_TIME = "last_update";
    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private UpdatesAdapter updatesAdapter = null;
    private PullToRefreshLayout mPullToRefreshLayout;
    private String branchName;
    private String repositoryName;

    private AppPreferences appPref = null;

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
        appPref = AppPreferences.getInstance();

        branchName = getBranchFromPreferences();
        repositoryName = getRepoFromPreferences();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)
                // We need to mark the ListView and it's Empty View as pullable
                // This is because they are not dirent children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                // We can now complete the setup as desired
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        if (repositoryName != null && branchName != null) {
                            new UpdatesFetcher(getActivity().getApplicationContext(), LastUpdatesFragment.this).execute(repositoryName, branchName);
                        }
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onStart() {
        super.onStart();
        new UpdatesFetcher(getActivity().getApplicationContext(), this).execute(repositoryName, branchName);
    }

    @Override
    public void onResume() {
        super.onResume();
        new UpdatesFetcher(getActivity().getApplicationContext(), this).execute(repositoryName, branchName);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView l = (ListView) inflater.inflate(R.layout.app_list_view, container, false);
        return l;
    }

    private String getBranchFromPreferences() {
        String[] arr = appPref.getBranches();
        return arr[arr.length - 1];
    }

    private String getRepoFromPreferences() {
        String[] arr = appPref.getRepositories();
        return arr[arr.length - 1];

    }

    /**
     * Just adapts the adapter.
     *
     * @param commits Items of the list.
     */
    @Override
    public void performAction(Collection<RepositoryCommit> commits) {
        // if (updatesAdapter == null)

        // Notify PullToRefreshLayout that the refresh has finished
        if(mPullToRefreshLayout.isRefreshing()){
            mPullToRefreshLayout.setRefreshComplete();
        }
        updatesAdapter = new UpdatesAdapter(getActivity(),
                R.layout.updates_list_item_layout,
                commits.toArray(new RepositoryCommit[commits.size()]));
        setListAdapter(updatesAdapter);

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
            branch.setText(repositoryName + "/" + branchName);

            TextView date = (TextView) row.findViewById(R.id.commit_date);
            date.setText(dateFormat.format(commit.getCommitter().getDate()));

            TextView message = (TextView) row.findViewById(R.id.commit_message);
            message.setText(commit.getMessage());

            TextView author = (TextView) row.findViewById(R.id.commit_user);
            author.setText("by " + commit.getCommitter().getName());

            return row;
        }
    }

    public PullToRefreshLayout getmPullToRefreshLayout() {
        return mPullToRefreshLayout;
    }
}
