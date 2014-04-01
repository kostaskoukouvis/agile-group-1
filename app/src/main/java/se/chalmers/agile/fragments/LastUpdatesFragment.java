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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.agile.R;
import se.chalmers.agile.core.Commit;

/**
 * Displays the last updates from the selected repositories.
 */
public class LastUpdatesFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1. Get the list of project/branches
        //List<ProjectBranch> branches = ...;
        //2. Fetch data from GitHub
        List<Commit> commits = new LinkedList<Commit>();
        commits.add(new Commit("agile", "master", new Date(), "john doe", "Hello world"));
        //3. Filtering

        //4. Display results
        // TODO: Change Adapter to display your content
        setListAdapter(new UpdatesAdapter(getActivity(),
                R.layout.updates_list_item_layout, commits.toArray(new Commit[commits.size()])));
    }

    /**
     * Adapter to show updates.
     */
    public class UpdatesAdapter extends ArrayAdapter<Commit> {

        Context context;
        int layoutResourceId;
        Commit data[] = null;

        public UpdatesAdapter(Context context, int layoutResourceId, Commit[] data) {
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
            Commit c = data[position];

            TextView branch = (TextView) row.findViewById(R.id.commit_branch);
            branch.setText(c.getBranch());

            TextView date = (TextView) row.findViewById(R.id.commit_date);
            if (c.getTime()!=null) {
                date.setText(c.getTime().toGMTString());
            }

            TextView message = (TextView) row.findViewById(R.id.commit_message);
            message.setText(c.getCommitMsg());

            TextView author = (TextView) row.findViewById(R.id.commit_user);
            author.setText(c.getCommitter());

            return row;
        }
    }
}
